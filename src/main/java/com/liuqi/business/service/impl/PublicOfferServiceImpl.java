package com.liuqi.business.service.impl;


import com.github.pagehelper.PageInfo;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.enums.PublicOfferStatusEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.RedisRepository;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.DateUtil;
import com.liuqi.utils.MathUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.PublicOfferMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class PublicOfferServiceImpl extends BaseServiceImpl<PublicOfferModel, PublicOfferModelDto> implements PublicOfferService {

    @Autowired
    private PublicOfferMapper publicOfferMapper;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private SearchPrice searchPrice;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private UserWalletLogService userWalletLogService;

    @Autowired
    private PublicOfferRecordService publicOfferRecordService;

    @Autowired
    private AddressRecordService addressRecordService;

    @Autowired
    private PoolProfitConfigService poolProfitConfigService;

    @Autowired
    private ConfigService configService;

    @Override
    public BaseMapper<PublicOfferModel, PublicOfferModelDto> getBaseMapper() {
        return this.publicOfferMapper;
    }

    @Override
    protected void doMode(PublicOfferModelDto dto) {
        super.doMode(dto);

        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void buy(Long userId, Long offerId, BigDecimal quantity) {
        PublicOfferModelDto publicOfferModelDto = this.getById(offerId);

        BigDecimal usedQuota = MathUtil.add(publicOfferModelDto.getUseQuota(), quantity);

        Boolean timeFlag = DateUtil.isBetween(publicOfferModelDto.getStartTime(), publicOfferModelDto.getOverTime());

        if (!timeFlag || PublicOfferStatusEnum.OVER.getCode().equals(publicOfferModelDto.getStatus())) {
            throw new BusinessException(MessageSourceHolder.getMessage("message95"));
        }

        if (usedQuota.compareTo(publicOfferModelDto.getQuota()) > 0) {
            throw new BusinessException(MessageSourceHolder.getMessage("message96"));
        }

        //各个级别公募额度
        Map<Integer, BigDecimal> limitMap = new HashMap<>();
        List<PoolProfitConfigModelDto> profitConfigModelDtos = poolProfitConfigService.queryListByDto(new PoolProfitConfigModelDto(), false);

        profitConfigModelDtos.forEach(dto -> {
            limitMap.put(dto.getLevel(), dto.getPublicOfferProfitAmount());
        });

        AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(userId);
        if (limitMap.get(addressRecordModelDto.getPoolLevel()) == null) {
            throw new BusinessException(MessageSourceHolder.getMessage("message97"));
        }
        BigDecimal limitAmount = limitMap.get(addressRecordModelDto.getPoolLevel());

        BigDecimal alreadyAmount = publicOfferRecordService.getAlreadyAmountByOfferId(userId, offerId);
        alreadyAmount = alreadyAmount == null ? BigDecimal.ZERO : alreadyAmount;

        if (MathUtil.add(alreadyAmount, quantity).compareTo(limitAmount) > 0) {
            throw new BusinessException(MessageSourceHolder.getMessage("message59")+":" + MathUtil.sub(MathUtil.add(alreadyAmount, quantity), limitAmount));
        }

        Long buyCurrencyId = publicOfferModelDto.getCurrencyId();
        //购买的币种价格
        BigDecimal currencyPrice  = searchPrice.getPrice(currencyService.getNameById(buyCurrencyId));

        //支付币种
        Long usdtId = currencyService.getUsdtId();

        BigDecimal usdtPrice = searchPrice.getUsdtQcPrice();

        //支付价格
        //当前币种价格 转 usdt价格， 乘以折扣价格
        BigDecimal payPrice = MathUtil.mul(MathUtil.mul(MathUtil.mul(currencyPrice, quantity), usdtPrice),publicOfferModelDto.getDiscountRate());

        //扣除usdt
        UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId, usdtId, MathUtil.zeroSub(payPrice));
        userWalletLogService.addLog(userId, usdtId, MathUtil.zeroSub(payPrice), WalletLogTypeEnum.PUBLIC_OFFER_BUY.getCode(),
                -1L, "公募购买扣除", userWalletModelDto);

        //添加购买币种
        userWalletModelDto = userWalletService.modifyWalletUsing(userId, buyCurrencyId, quantity);
        userWalletLogService.addLog(userId, buyCurrencyId, quantity, WalletLogTypeEnum.PUBLIC_OFFER_BUY.getCode(),
                -1L, "公募购买", userWalletModelDto);

        //添加记录
        publicOfferRecordService.addRecord(userId, offerId, quantity,buyCurrencyId,currencyPrice,payPrice);

        publicOfferModelDto.setUseQuota(usedQuota);

        if (usedQuota.compareTo(publicOfferModelDto.getQuota()) == 0) {
            publicOfferModelDto.setStatus(PublicOfferStatusEnum.OVER.getCode());
        }

        this.update(publicOfferModelDto);
    }

   /* @Async
    public void profit(Long offerId) {
        String key = "public_offer:profit:id:"+offerId;
        Object target = redisRepository.get(key);
        if (target != null){
            System.out.println("id: "+ offerId +" 奖励正在执行。。。");
            return;
        }
        List<Long> userIds = publicOfferRecordService.getAddresIdsByOfferId(offerId);

        //各个级别奖励
        Map<Integer, BigDecimal> profitMap = new HashMap<>();
        List<PoolProfitConfigModelDto> profitConfigModelDtos = poolProfitConfigService.queryListByDto(new PoolProfitConfigModelDto(), false);

        profitConfigModelDtos.forEach(dto -> {
            profitMap.put(dto.getLevel(), dto.getPublicOfferProfitAmount());
        });
        Long ptId = currencyService.getPTId();
        for (Long userId : userIds) {
            AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(userId);

            if (addressRecordModelDto.getPoolLevel() == 0) {
                continue;
            }
            BigDecimal amount = profitMap.get(addressRecordModelDto.getPoolLevel());

            UserWalletModelDto u = userWalletService.modifyWalletUsing(userId, ptId, amount);
            userWalletLogService.addLog(userId, ptId, amount, WalletLogTypeEnum.PUBLIC_OFFER_PROFIT.getCode(), -1L,
                    "公募奖励", u);
        }

        redisRepository.del(key);
    }

    public void profitJob() {
        PublicOfferModelDto search = new PublicOfferModelDto();
        search.setStatus(PublicOfferStatusEnum.ING.getCode());
        List<PublicOfferModelDto> list = this.queryListByDto(search, false);

        list.forEach(dto -> {
			boolean flag = DateUtil.isBetween(dto.getStartTime(),dto.getOverTime());

			if (!flag){
			    return;
            }

			this.profit(dto.getId());
			//更新状态
			dto.setStatus(PublicOfferStatusEnum.OVER.getCode());
			this.update(dto);

        });


    }*/

}
