package com.liuqi.business.service.impl;


import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.CrowdfundInfoStatusEnum;
import com.liuqi.business.enums.CrowdfundStatusEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.enums.WalletTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateUtil;
import com.liuqi.utils.MathUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.CrowdfundInfoMapper;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CrowdfundInfoServiceImpl extends BaseServiceImpl<CrowdfundInfoModel, CrowdfundInfoModelDto> implements CrowdfundInfoService {

    @Autowired
    private CrowdfundInfoMapper crowdfundInfoMapper;

    @Autowired
    private CrowdfundService crowdfundService;

    @Autowired
    private CrowdfundRecordService crowdfundRecordService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private UserWalletLogService userWalletLogService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private AddressRecordService addressRecordService;

    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;

    @Autowired
    private ActiveRecordService activeRecordService;

    @Autowired
    private CrowdfundRewardConfigService rewardConfigService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<CrowdfundInfoModel, CrowdfundInfoModelDto> getBaseMapper() {
        return this.crowdfundInfoMapper;
    }


    public CrowdfundInfoModelDto getEnableInfoByFundId(Long crowdfundId) {
        return crowdfundInfoMapper.getEnableInfoByFundId(crowdfundId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void buy(Long addressId, Long infoId, BigDecimal amount) {
        CrowdfundInfoModelDto infoDto = this.getById(infoId);
        CrowdfundModelDto fundDto = crowdfundService.getById(infoDto.getCrowdfundId());

        //用户已购买数量
        BigDecimal userUsedQuantity = crowdfundRecordService.getTotalByAddressId(addressId, infoId);

        //购买额度不能大于限制最大额度
        if (MathUtil.add(userUsedQuantity, amount).compareTo(infoDto.getMaxQuota()) > 0) {
            throw new BusinessException(MessageSourceHolder.getMessage("message59")+":" + MathUtil.sub(MathUtil.add(userUsedQuantity, amount), infoDto.getMaxQuota()));
        }

        if (!CrowdfundInfoStatusEnum.ING.getCode().equals(infoDto.getStatus())
                || !CrowdfundStatusEnum.ENABLE.getCode().equals(fundDto.getStatus())) {
            throw new BusinessException(MessageSourceHolder.getMessage("message60"));
        }

        boolean flag = DateUtil.isBetween(infoDto.getStartTime(), infoDto.getEndTime());
        if (!flag) {
            throw new BusinessException(MessageSourceHolder.getMessage("message61")+": " + infoDto.getStartTime() + "-" + infoDto.getEndTime());
        }

        //购买额度 大于 总额度
        BigDecimal usedQuota = MathUtil.add(infoDto.getUsedQuota(), amount);
        if (usedQuota.compareTo(infoDto.getTotalQuota()) > 0) {
            throw new BusinessException(MessageSourceHolder.getMessage("message62"));
        }

        BigDecimal chargeRate = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_CROWDFUND_CHARGE_AFIL_RATE));
        BigDecimal afilPrice = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_PRICE_AFIL));

        //购买的AFIl数量
        BigDecimal afilAmount = MathUtil.mul(afilPrice, amount);

        BigDecimal chargeCost = MathUtil.mul(afilAmount, chargeRate);

        Long ptId = currencyService.getPTId();

        Long currencyId = fundDto.getCurrencyId();
        UserWalletModelDto userWalletModelDto = userWalletService.modifyWallet(addressId, currencyId, MathUtil.zeroSub(amount),amount);
        userWalletLogService.addLog(addressId, currencyId, MathUtil.zeroSub(amount), WalletLogTypeEnum.BUY_CROWDFUND.getCode(),
                -1L, "投票扣费", userWalletModelDto);

        userWalletModelDto = userWalletService.modifyWalletFreeze(addressId, ptId, afilAmount);
        userWalletLogService.addLog(addressId, ptId, afilAmount, WalletLogTypeEnum.CROWDFUND_BUY_AFIL.getCode(),
                -1L, "投票认购AFIL", userWalletModelDto);


        userWalletModelDto = userWalletService.modifyWalletUsing(addressId, ptId, MathUtil.zeroSub(chargeCost));
        userWalletLogService.addLog(addressId, ptId, MathUtil.zeroSub(chargeCost), WalletLogTypeEnum.CROWDFUND_SERVICE_CHARGE.getCode(),
                -1L, "投票手续费", userWalletModelDto);

        //添加购买记录
        crowdfundRecordService.addRecord(addressId, infoId, amount, currencyId, afilAmount, ptId,afilPrice);

        //到达总额，结束这一期投票
        if (MathUtil.add(infoDto.getUsedQuota(), amount).compareTo(BigDecimal.ZERO) == 0) {
            infoDto.setStatus(CrowdfundInfoStatusEnum.OVER.getCode());
            //计算收益
            this.overProfit(infoDto, fundDto.getId(), currencyId);
        }

        infoDto.setUsedQuota(usedQuota);
        this.update(infoDto);

    }

    @Transactional(rollbackFor = Throwable.class)
    public void profit() {
        CrowdfundModelDto search = new CrowdfundModelDto();
        search.setStatus(CrowdfundStatusEnum.ENABLE.getCode());
        List<CrowdfundModelDto> fundList = crowdfundService.queryListByDto(search, false);

        for (CrowdfundModelDto crowdfundModelDto : fundList) {
            Long currencyId = crowdfundModelDto.getCurrencyId();

            CrowdfundInfoModelDto infoModelDto = getEnableInfoByFundId(crowdfundModelDto.getId());
            if (infoModelDto == null) {
                continue;
            }
            boolean flag = DateUtil.isBetween(infoModelDto.getStartTime(), infoModelDto.getEndTime());
            if (flag) {
                continue;
            }
            String key = KeyConstant.KEY_CROWDFUND_PROFIT_JOB + infoModelDto.getId();
            Object execute = redisRepository.get(key);
            if (execute == null) {
                redisRepository.set(key, 1);
                this.overProfit(infoModelDto, crowdfundModelDto.getId(), currencyId);
            } else {
                System.out.println(infoModelDto.getId() + " 收益正在计算中。。。");
            }
        }
    }

    //结束计算收益
    public void overProfit(CrowdfundInfoModelDto infoModelDto, Long fundId, Long currencyId) {
        String key = KeyConstant.KEY_CROWDFUND_PROFIT_JOB + infoModelDto.getId();
        //配置参数
        Integer limit = Integer.valueOf(configService.queryValueByName(ConfigConstant.CONFIG_CROWDFUND_PROFIT_COUNT));

        CrowdfundModelDto crowdfundModelDto = crowdfundService.getById(infoModelDto.getCrowdfundId());

        CrowdfundRewardConfigModelDto configModelDto = new CrowdfundRewardConfigModelDto();
        configModelDto.setStatus(1);
        List<CrowdfundRewardConfigModelDto> rewardConfigList = rewardConfigService.queryListByDto(configModelDto, false);

        BigDecimal noFinishRefundRate = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_CROWDFUND_NO_FINISH_REFUND_RATE));

        BigDecimal noFinishRefundAFILRate = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_CROWDFUND_NO_FINISH_REFUND_AFIL_RATE));

        List<Map<String, Object>> nowInfoSummary = crowdfundRecordService.getSummaryByInfoId(infoModelDto.getId());
        //额度不达标
        if (infoModelDto.getUsedQuota().compareTo(infoModelDto.getTotalQuota()) != 0) {
            //当期退还本金
            for (Map<String, Object> summary : nowInfoSummary) {
                Long addressId = Long.valueOf(String.valueOf(summary.get("addressId")));
                BigDecimal total = new BigDecimal(String.valueOf(summary.get("total")));

                this.refundCrowdfund(addressId, currencyId, total);
            }
            this.refundCrowdfundByNoFinished(infoModelDto, fundId, currencyId, noFinishRefundAFILRate, noFinishRefundRate);

            //更新状态
            infoModelDto.setStatus(CrowdfundInfoStatusEnum.OVER.getCode());
            this.update(infoModelDto);

            //整个投票环节结束
            crowdfundModelDto.setStatus(CrowdfundStatusEnum.OVER.getCode());
            crowdfundService.update(crowdfundModelDto);
            redisRepository.del(key);
            return;
        }

        System.out.println("开始计算收益");

        Integer nowIndex = infoModelDto.getIndex();

        if (nowIndex < limit) {
            redisRepository.del(key);
            return;
        }


        //第三期 第一期结算收益，第四期 第二期结算
        Integer profitIndex = nowIndex - limit + 1;

        CrowdfundInfoModelDto profitInfoDto = this.getByIndex(profitIndex, infoModelDto.getCrowdfundId());


        List<Map<String, Object>> infoSummary = crowdfundRecordService.getSummaryByInfoId(profitInfoDto.getId());

        BigDecimal staticProfitRate = profitInfoDto.getStaticRewardRate();

        BigDecimal directRewordRate = profitInfoDto.getDirectRewardRate();

        for (Map<String, Object> summary : infoSummary) {
            Long addressId = Long.valueOf(String.valueOf(summary.get("addressId")));
            BigDecimal total = new BigDecimal(String.valueOf(summary.get("total")));

            this.refundCrowdfund(addressId, currencyId, total);

            //静态收益
            BigDecimal staticProfit = MathUtil.mul(total, staticProfitRate);

            UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(addressId, currencyId, staticProfit);
            userWalletLogService.addLog(addressId, currencyId, staticProfit, WalletLogTypeEnum.CROWDFUND_STATIC_PROFIT.getCode(),
                    -1L, "投票静态收益", userWalletModelDto);

            //直推奖励
            Long parentId = activeRecordService.getParentByToAddressId(addressId);

            BigDecimal directReward = MathUtil.mul(total, directRewordRate);
            userWalletModelDto = userWalletService.modifyWalletUsing(parentId, currencyId, directReward);
            userWalletLogService.addLog(parentId, currencyId, directReward, WalletLogTypeEnum.CROWDFUND_DIRECT_REWARD.getCode(),
                    -1L, "投票直推奖励", userWalletModelDto);

            //团队奖励
            BigDecimal teamReward = this.getTeamReward(addressId, infoModelDto.getId(), rewardConfigList);

            userWalletModelDto = userWalletService.modifyWalletUsing(addressId, currencyId, teamReward);
            userWalletLogService.addLog(addressId, currencyId, teamReward, WalletLogTypeEnum.CROWDFUND_TEAM_REWARD.getCode(),
                    -1L, "投票团队奖励", userWalletModelDto);

        }
        //状态改为已结算
        profitInfoDto.setStatus(CrowdfundInfoStatusEnum.SETTLEMENT.getCode());
        this.update(profitInfoDto);

        //状态改为已结束
        infoModelDto.setStatus(CrowdfundInfoStatusEnum.OVER.getCode());
        this.update(infoModelDto);
        redisRepository.del(key);
    }

    //未完成投票，结算
    public void refundCrowdfundByNoFinished(CrowdfundInfoModelDto nowInfoModel, Long foundId,
                                            Long currencyId, BigDecimal noFinishRefundAFILRate, BigDecimal noFinishRefundRate) {
        //前面未结算期 退还本金50%，剩余50% 转换未AFIL 并扩大1.5倍
        Integer preIndex = nowInfoModel.getIndex() - 1;

        CrowdfundInfoModelDto preInfoDto = this.getByIndex(preIndex, foundId);

        if (preInfoDto == null || CrowdfundInfoStatusEnum.SETTLEMENT.getCode().equals(preInfoDto.getStatus())) {
            return;
        }

        //平台币种id
        Long ptId = currencyService.getPTId();

        List<Map<String, Object>> preSummary = crowdfundRecordService.getSummaryByInfoId(preInfoDto.getId());
        for (Map<String, Object> summary : preSummary) {
            Long addressId = Long.valueOf(String.valueOf(summary.get("addressId")));
            BigDecimal total = new BigDecimal(String.valueOf(summary.get("total")));
            BigDecimal totalGainQuantity = new BigDecimal(String.valueOf(summary.get("totalGainQuantity")));

            //退还本金50%
            BigDecimal realAmount = MathUtil.mul(total, noFinishRefundRate);
            //this.refundCrowdfund(addressId, currencyId, realAmount);
            UserWalletModelDto userWalletModelDto = userWalletService.modifyWallet(addressId, currencyId, realAmount,MathUtil.zeroSub(total));
            userWalletLogService.addLog(addressId, currencyId, realAmount, WalletLogTypeEnum.CROWDFUND_REFUND.getCode(),
                    -1L, "投票本金返还", userWalletModelDto);
            //

            //剩余返还AFIL,扩大N倍
            BigDecimal afilTotal = MathUtil.mul(totalGainQuantity, noFinishRefundAFILRate);

            userWalletModelDto = userWalletService.modifyWallet(addressId, ptId, afilTotal,MathUtil.zeroSub(totalGainQuantity));
            userWalletLogService.addLog(addressId, ptId, afilTotal, WalletLogTypeEnum.CROWDFUND_REFUND_AFIL.getCode(),
                    -1L, "投票本金返还AFIL", userWalletModelDto);
        }
        preInfoDto.setStatus(CrowdfundInfoStatusEnum.SETTLEMENT.getCode());
        this.update(preInfoDto);

        refundCrowdfundByNoFinished(preInfoDto, foundId, currencyId, noFinishRefundAFILRate, noFinishRefundRate);

    }

    /**
     * 获取团队奖励比例
     *
     * @return
     */
    private BigDecimal getTeamReward(Long addressId, Long fundInfoId, List<CrowdfundRewardConfigModelDto> rewardConfigList) {
        BigDecimal teamRewardRate = BigDecimal.ZERO;
        //直推数
        List<Long> directIds = userLevelService.getAssignSubIdList(addressId, 1);

        int subIndex = 0;
        BigDecimal totalTeamCrowdfund = BigDecimal.ZERO;

        //最大团队投票数
        BigDecimal maxTeamCrowdfund = BigDecimal.ZERO;

        for (Long directId : directIds) {
            List<Long> allSubIds = userLevelService.getAllSubIdList(directId);

            BigDecimal teamCrowdfund = BigDecimal.ZERO;

            for (Long subId : allSubIds) {
                teamCrowdfund = MathUtil.add(teamCrowdfund, crowdfundRecordService.getTotalByAddressId(subId, fundInfoId));
            }

            totalTeamCrowdfund = MathUtil.add(totalTeamCrowdfund, teamCrowdfund);

            if (maxTeamCrowdfund.compareTo(teamCrowdfund) < 0) {
                maxTeamCrowdfund = teamCrowdfund;
            }

            if (subIndex < allSubIds.size()) {
                subIndex = allSubIds.size();
            }
        }

        //去掉最大区
        totalTeamCrowdfund = MathUtil.sub(totalTeamCrowdfund, maxTeamCrowdfund);


        for (CrowdfundRewardConfigModelDto rewardConfigModelDto : rewardConfigList) {

            if (totalTeamCrowdfund.compareTo(rewardConfigModelDto.getTotalAmount()) >= 0
                    && directIds.size() >= rewardConfigModelDto.getDirectCount() &&
                    (subIndex >= rewardConfigModelDto.getMinSubCount() && subIndex <= rewardConfigModelDto.getMaxSubCount())) {
                teamRewardRate = rewardConfigModelDto.getRewardRate();
            }
        }

        return MathUtil.mul(totalTeamCrowdfund, teamRewardRate);
    }


    private CrowdfundInfoModelDto getByIndex(Integer index, Long crowdfundId) {
        return crowdfundInfoMapper.getByIndex(index, crowdfundId);
    }


    private void refundCrowdfund(Long addressId, Long currencyId, BigDecimal total) {
        UserWalletModelDto userWalletModelDto = userWalletService.modifyWallet(addressId, currencyId, total,MathUtil.zeroSub(total));
        userWalletLogService.addLog(addressId, currencyId, total, WalletLogTypeEnum.CROWDFUND_REFUND.getCode(),
                -1L, "投票本金返还", userWalletModelDto);
    }

    public void release() {
        UserWalletLogModelDto userWalletLogModelDto = new UserWalletLogModelDto();
        userWalletLogModelDto.setType(WalletLogTypeEnum.CROWDFUND_REFUND_AFIL.getCode());
        List<UserWalletLogModelDto> list = userWalletLogService.queryListByDto(userWalletLogModelDto, false);

        BigDecimal releaseRate = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_CROWDFUND_AFIL_RELEASE_RATE));
        for (UserWalletLogModelDto logModelDto : list) {
            Long userId = logModelDto.getUserId();
            Long currencyId = logModelDto.getCurrencyId();

            BigDecimal freezeAmount = logModelDto.getMoney();

            //已经释放的数量
            BigDecimal alreadyReleaseAmount = userWalletLogService.getMoneyByOrderId(logModelDto.getId());
            if (freezeAmount.compareTo(alreadyReleaseAmount) == 0) {
                continue;
            }

            BigDecimal releaseAmount = MathUtil.mul(freezeAmount, releaseRate);

            if (MathUtil.add(alreadyReleaseAmount, releaseAmount).compareTo(freezeAmount) > 0) {
                releaseAmount = MathUtil.sub(freezeAmount, alreadyReleaseAmount);
            }

            UserWalletModel userWalletModel = userWalletService.modifyWallet(userId, currencyId, releaseAmount, MathUtil.zeroSub(releaseAmount));
            userWalletLogService.addLog(userId, currencyId, releaseAmount, WalletLogTypeEnum.CROWDFUND_AFIL_RELEASE.getCode(), logModelDto.getId(),
                    "投票AFIL释放", userWalletModel);

        }
    }

    @Override
    public CrowdfundInfoModelDto getLastInfoByFundId(Long fundId) {
        return crowdfundInfoMapper.getLastInfoByFundId(fundId);
    }

    /* public void userLevel(){
        CrowdfundRewardConfigModelDto configModelDto = new CrowdfundRewardConfigModelDto();
        configModelDto.setStatus(1);
        List<CrowdfundRewardConfigModelDto> rewardConfigList = rewardConfigService.queryListByDto(configModelDto, false);
        List<Long> addressIds = addressRecordService.getAll();

        for (Long addressId : addressIds){

        }
        BigDecimal teamRewardRate = BigDecimal.ZERO;
        //直推数
        List<Long> directIds = userLevelService.getAssignSubIdList(addressId, 1);

        int subIndex = 0;
        BigDecimal totalTeamCrowdfund = BigDecimal.ZERO;

        //最大团队众筹数
        BigDecimal maxTeamCrowdfund = BigDecimal.ZERO;

        for (Long directId : directIds) {
            List<Long> allSubIds = userLevelService.getAllSubIdList(directId);

            BigDecimal teamCrowdfund = BigDecimal.ZERO;

            for (Long subId : allSubIds) {
                teamCrowdfund = MathUtil.add(teamCrowdfund, crowdfundRecordService.getTotalByAddressId(subId, fundInfoId));
            }

            totalTeamCrowdfund = MathUtil.add(totalTeamCrowdfund, teamCrowdfund);

            if (maxTeamCrowdfund.compareTo(teamCrowdfund) < 0) {
                maxTeamCrowdfund = teamCrowdfund;
            }
            if (subIndex < allSubIds.size()) {
                subIndex = allSubIds.size();
            }
        }

        //去掉最大区
        totalTeamCrowdfund = MathUtil.sub(totalTeamCrowdfund, maxTeamCrowdfund);


        for (CrowdfundRewardConfigModelDto rewardConfigModelDto : rewardConfigList) {

            if (totalTeamCrowdfund.compareTo(rewardConfigModelDto.getTotalAmount()) >= 0
                    && directIds.size() >= rewardConfigModelDto.getDirectCount() &&
                    (subIndex >= rewardConfigModelDto.getMinSubCount() && subIndex <= rewardConfigModelDto.getMaxSubCount())) {
                teamRewardRate = rewardConfigModelDto.getRewardRate();
            }
        }


    }*/

}
