package com.liuqi.business.service.impl;

import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.dto.HoldingDto;
import com.liuqi.business.dto.RankStatisticDto;
import com.liuqi.business.enums.ActiveStatusEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.utils.MathUtil;
import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.wallet.Protos;
import org.bouncycastle.pqc.math.linearalgebra.BigEndianConversions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoinHoldProfitHandleServiceImpl implements CoinHoldProfitHandleService {

    @Autowired
    private UserPoolWalletService userWalletService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private UserPoolWalletLogService userWalletLogService;
    @Autowired
    private CurrencyHoldingService currencyHoldingService;
    @Autowired
    private PoolStatisticService poolStatisticService;
    @Autowired
    private PoolProfitRecordService poolProfitRecordService;
    @Autowired
    private ComputePowerRecordService computePowerRecordService;

    @Autowired
    private PoolProfitConfigService poolProfitConfigService;

    @Autowired
    private AddressRecordService addressRecordService;

    @Autowired
    private ConfigService configService;

    @Override
    @Transactional
    public void doMining(Long currencyId) {
        CurrencyConfigModelDto currencyConfigModelDto = currencyConfigService.getByCurrencyId(currencyId);
        if (!SwitchEnum.isOn(currencyConfigModelDto.getMiningSwitch())) {
            log.info("币种{}未开启挖矿，跳过", currencyConfigModelDto.getCurrencyName());
            return;
        }

        //矿池直推奖励比例
        BigDecimal poolDirectRete = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_POOL_DIRECT_PROFIT_RATE));
        // 昨日收益和持币算力和推广算力清零
        poolStatisticService.resetAll(currencyId);

        BigDecimal minHolding = currencyConfigModelDto.getMinHolding();

        UserPoolWalletModelDto params = new UserPoolWalletModelDto();
        params.setCurrencyId(currencyId);
        List<UserPoolWalletModelDto> userWalletModelDtos = userWalletService.queryListByDto(params, false);

        //矿池配置
        List<PoolProfitConfigModelDto> profitConfigModelDtos = poolProfitConfigService.queryListByDto(new PoolProfitConfigModelDto(), false);

        Map<Integer, BigDecimal> dynamicProfitRateMap = new HashMap<>();
        profitConfigModelDtos.forEach(dto -> {
            dynamicProfitRateMap.put(dto.getLevel(), dto.getProfitRate());
        });
        dynamicProfitRateMap.put(0,BigDecimal.ZERO);

        // 持币100以上的才能参与每日收益
        userWalletModelDtos = userWalletModelDtos.stream().filter(m -> m.getUsing().compareTo(minHolding) >= 0).collect(Collectors.toList());

        // 按持币量排名[从低到高]
        Collections.sort(userWalletModelDtos, Comparator.comparing(UserPoolWalletModelDto::getUsing));
        List<RankStatisticDto> list = calculateRank(userWalletModelDtos);

        Integer i = 1;
        // 钱包ID - 排名
        Map<Long, Integer> addressHoldingRankMap = new HashMap<>();
        for (RankStatisticDto m : list) {
            for (UserPoolWalletModelDto p : m.getList()) {
                addressHoldingRankMap.put(p.getId(), i);
            }
            i += m.getList().size();
        }
        // 排名总和
        Integer totalRank = addressHoldingRankMap.values().stream().reduce(Integer::sum).orElse(0);
        HoldingDto maxProfitDto = HoldingDto.builder().profit(BigDecimal.ZERO).profitRatio(BigDecimal.ZERO).build();

        //计算当天矿池总量
        //从矿池转出的总数量
        BigDecimal outPutAmount = userWalletLogService.getMoneyByLogType(WalletLogTypeEnum.OUTPUT.getCode(), currencyId);
        //从转入矿池的数量
        BigDecimal inPutAmount = userWalletLogService.getMoneyByLogType(WalletLogTypeEnum.INPUT.getCode(), currencyId);
        /*
            增加算法 273万+转入矿池AIFL数量*0.15/30天
            减少算法 273万-转出矿池AIFL数量*0.15/30天
        */
        //静态矿池天数
        BigDecimal staticCoinDays = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_POOL_STATIC_COIN_DAYS));
        //静态矿池总数量比例
        BigDecimal staticAmountCoinRate = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_POOL_STATIC_COIN_AMOUNT_RATE));
        //动态矿池数量比例
        BigDecimal dynamicAmountCoinRate = new BigDecimal(configService.queryValueByName(ConfigConstant.CONFIG_POOL_DYNAMIC_COIN_RATE));
        //静态矿池总量
        BigDecimal staticTotalCoin = MathUtil.div(MathUtil.mul(MathUtil.add(currencyConfigModelDto.getBlockAmount(),
                MathUtil.sub(inPutAmount, outPutAmount)), staticAmountCoinRate), staticCoinDays);
        //动态矿池总量
//        BigDecimal dynamicTotalCoin = MathUtil.mul(staticTotalCoin, dynamicAmountCoinRate);


        //所有的静态收益
        Map<Long,BigDecimal> userStaticProfitMap = new HashMap<>();

        // 计算持币收益
        for (Map.Entry<Long, Integer> item : addressHoldingRankMap.entrySet()) {
            UserPoolWalletModelDto u = userWalletService.getById(item.getKey(), false);
            log.info("持币量=" + u.getUsing() + "-排名=" + item.getValue());

            HoldingDto profitRatio = calculateStaticProfit(item.getKey(), item.getValue(), totalRank,
                    staticTotalCoin,userStaticProfitMap);

            if (profitRatio.getProfitRatio().compareTo(maxProfitDto.getProfitRatio()) > 0) {
                maxProfitDto = profitRatio;
            }

            // 更新地址的今日收益和累计收益
            poolStatisticService.addOrUpdateProfit(u.getUserId(), currencyId, profitRatio.getProfit());
            // 添加用户的静态收益及收益率统计
            poolProfitRecordService.addStaticRecord(u.getUserId(), currencyId, profitRatio.getProfit(), profitRatio.getProfitRatio());
        }
        // -----------------------动态
        // 用户id - 动态收益
        addressHoldingRankMap.forEach((k, v) -> {

            UserPoolWalletModelDto u = userWalletService.getById(k, false);
            UserLevelModelDto levelModelDto = userLevelService.getByUserId(u.getUserId());

            AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(u.getUserId());

            //直推奖励，奖励给直推
            if (levelModelDto.getParentId() != 0){
                BigDecimal staticProfit = userStaticProfitMap.get(u.getUserId());
                this.directDynamicProfit(levelModelDto.getParentId(),u.getCurrencyId(),poolDirectRete, staticProfit);
                poolStatisticService.addOrUpdatePower(levelModelDto.getParentId(), currencyId, MathUtil.mul(poolDirectRete, staticProfit));
            }

            // 获取当前地址的直推地址
            List<Long> directSubList = userLevelService.getAssignSubIdList(u.getUserId(), 1);

            if (directSubList == null || directSubList.isEmpty()) {
                return;
            }

            //最大静态收益
            BigDecimal maxTeamStaticProfitAmount = BigDecimal.ZERO;
            Long maxUserId = -1L;
            Integer maxPoolLevel = 0;

            //团队下总静态收益
            BigDecimal totalTeamStaticProfitAmount = BigDecimal.ZERO;

            for (Long directId : directSubList) {
                AddressRecordModelDto directDto = addressRecordService.getById(directId);
                //直推下级，
                List<Long> allSubCount = userLevelService.getAllSubIdList(directId);

                BigDecimal teamCoinAmount = BigDecimal.ZERO;

                for (Long subUserId : allSubCount){
                    AddressRecordModelDto sub = addressRecordService.getById(subUserId);
                    if (!ActiveStatusEnum.ACTIVE.getCode().equals(sub.getActive())){
                        continue;
                    }
                    // 抓取直推的静态收益
                    BigDecimal directSubUserHoldTotalCoin = userStaticProfitMap.get(subUserId) == null ? BigDecimal.ZERO : userStaticProfitMap.get(subUserId);
                    teamCoinAmount = MathUtil.add(teamCoinAmount,directSubUserHoldTotalCoin);
                }
                totalTeamStaticProfitAmount = MathUtil.add(totalTeamStaticProfitAmount, teamCoinAmount);

                if (teamCoinAmount.compareTo(maxTeamStaticProfitAmount) > 0) {
                    maxUserId = directId;
                    maxTeamStaticProfitAmount = teamCoinAmount;
                    maxPoolLevel = directDto.getPoolLevel();
                }
                BigDecimal profitRate = dynamicProfitRateMap.get(directDto.getPoolLevel());

                //添加推广算力记录
                computePowerRecordService.add(directId, u.getUserId(), currencyId, MathUtil.mul(teamCoinAmount,profitRate),
                        YesNoEnum.NO.getCode());
            }

            if (maxUserId != -1){
                BigDecimal maxProfitRate = dynamicProfitRateMap.get(maxPoolLevel);

                //添加最大区
                computePowerRecordService.add(maxUserId, u.getUserId(), currencyId, MathUtil.mul(maxTeamStaticProfitAmount,maxProfitRate), YesNoEnum.YES.getCode());
            }

            BigDecimal profitRate = dynamicProfitRateMap.get(addressRecordModelDto.getPoolLevel());
            //推广收益
            calculateDynamicProfit(u.getUserId(), currencyId, profitRate, totalTeamStaticProfitAmount);

            poolStatisticService.addOrUpdatePower(u.getUserId(), currencyId, MathUtil.mul(totalTeamStaticProfitAmount, profitRate));

        });

        // 添加/更新  该币种的最佳持币量和最佳持币对应的收益
        currencyHoldingService.addOrUpdate(currencyId, minHolding, maxProfitDto.getHolding(), maxProfitDto.getProfit());
    }

    /**
     * 统计持币排名
     **/
    private List<RankStatisticDto> calculateRank(List<UserPoolWalletModelDto> userWalletModelDtos) {
        List<RankStatisticDto> list = new ArrayList<>();
        userWalletModelDtos.forEach(m -> {
            if (list.isEmpty()) {//
                List<UserPoolWalletModelDto> tlist = new ArrayList<>();
                tlist.add(m);
                list.add(RankStatisticDto.builder().using(m.getUsing()).list(tlist).build());
            } else {
                RankStatisticDto lastDto = list.get(list.size() - 1);
                if (lastDto.getUsing().compareTo(m.getUsing()) == 0) {// 跟最后一个持币量一致
                    lastDto.getList().add(m);
                    list.set(list.size() - 1, lastDto);
                } else {// 价格不一致
                    List<UserPoolWalletModelDto> tlist = new ArrayList<>();
                    tlist.add(m);
                    list.add(RankStatisticDto.builder().using(m.getUsing()).list(tlist).build());
                }
            }
        });
        return list;
    }

    //直推奖励
    private void directDynamicProfit(Long userId, Long currencyId, BigDecimal rate, BigDecimal totalCoin) {
        BigDecimal profit = MathUtil.mul(totalCoin, rate);
        UserPoolWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId, currencyId, profit);
        userWalletLogService.addLog(userId, currencyId, profit, WalletLogTypeEnum.POOL_DIRECT_PROFIT.getCode(), userId,
                WalletLogTypeEnum.POOL_DIRECT_PROFIT.getName(), userWalletModelDto);
        // 添加动态收益
        poolProfitRecordService.addDynamicRecord(userId, currencyId, profit);
    }

    // 动态收益
    private void calculateDynamicProfit(Long userId, Long currencyId, BigDecimal rate, BigDecimal totalCoin) {
        BigDecimal profit = MathUtil.mul(totalCoin, rate);
        UserPoolWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId, currencyId, profit);
        userWalletLogService.addLog(userId, currencyId, profit, WalletLogTypeEnum.INVITE_MINING.getCode(), userId,
                WalletLogTypeEnum.INVITE_MINING.getName(), userWalletModelDto);
        // 添加动态收益
        poolProfitRecordService.addDynamicRecord(userId, currencyId, profit);
    }

    private BigDecimal calculateHashRate(BigDecimal teamHolding) {
        if (teamHolding.compareTo(BigDecimal.valueOf(10000)) <= 0) {// 小于等于10000，直接乘以10
            return MathUtil.mul(teamHolding, BigDecimal.valueOf(10));
        } else {// 大于10000，拿出来10000放大10倍，再加上减掉拿出去的10000的余额
            BigDecimal subTeamHolding = MathUtil.sub(teamHolding, BigDecimal.valueOf(10000));
            return MathUtil.add(BigDecimal.valueOf(10 * 10000), subTeamHolding);
        }
    }

    // 静态收益
    private HoldingDto calculateStaticProfit(Long walletId, Integer rank, Integer totalRank, BigDecimal totalCoin,
                                             Map<Long,BigDecimal> userStaticProfitMap) {
        UserPoolWalletModelDto userWalletModelDto = userWalletService.getById(walletId, false);

        BigDecimal ratio = MathUtil.div(BigDecimal.valueOf(rank), BigDecimal.valueOf(totalRank));

        BigDecimal profit = MathUtil.mul(totalCoin, ratio);

        userWalletModelDto = userWalletService.modifyWalletUsing(userWalletModelDto.getUserId(), userWalletModelDto.getCurrencyId(), profit);
        log.info("钱包id=" + walletId + "收益=" + profit);

        userStaticProfitMap.put(userWalletModelDto.getUserId(),profit);

        userWalletLogService.addLog(userWalletModelDto.getUserId(), userWalletModelDto.getCurrencyId(), profit, WalletLogTypeEnum.MINING.getCode(),
                userWalletModelDto.getUserId(), WalletLogTypeEnum.MINING.getName(), userWalletModelDto);

        return HoldingDto.builder().userId(userWalletModelDto.getUserId()).profitRatio(MathUtil.div(ratio, userWalletModelDto.getUsing())).profit(profit)
                .holding(userWalletModelDto.getUsing()).build();
    }

    //矿池等级
    public void userPoolLevel() {
        List<Long> addressIds = addressRecordService.getAll();
        List<PoolProfitConfigModelDto> profitConfigModelDtos = poolProfitConfigService.queryListByDto(new PoolProfitConfigModelDto(), false);

        for (Long addressId : addressIds) {
            AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(addressId);
            System.out.println(addressId);
            // 获取当前地址的直推地址
            List<Long> directSubList = userLevelService.getAssignSubIdList(addressId, 1);

            if (directSubList == null || directSubList.isEmpty()) {
                continue;
            }

            //多少代
            int maxSubCount = userLevelService.getAllSubIdList(addressId).size();

            //总有效人数
            Integer totalValidCount = 0;

            //最大区有效人数
            Integer mexValidCount = 0;
            for (Long directId : directSubList) {
                //直推下级，
                List<Long> allSubCount = userLevelService.getAllSubIdList(directId);

                Integer teamValidCount = 1;

                for (Long subUserId : allSubCount) {
                    AddressRecordModelDto subRecordDto = addressRecordService.getById(subUserId);
                    if (!ActiveStatusEnum.ACTIVE.getCode().equals(subRecordDto.getActive())) {
                        continue;
                    }
                    //团队有效人数
                    teamValidCount++;
                }

                totalValidCount += teamValidCount;

                if (teamValidCount > mexValidCount) {
                    mexValidCount = teamValidCount;
                }
            }
            //去掉最大区
            if (directSubList.size()>1){
                totalValidCount = totalValidCount - mexValidCount;
            }
            Integer poolLevel = 0;
            //推广等级
            for (PoolProfitConfigModelDto configModelDto : profitConfigModelDtos) {
                if (configModelDto.getDirectCount().compareTo(directSubList.size()) >= 0
                        && configModelDto.getValidCount().compareTo(totalValidCount) >= 0
                        && (maxSubCount >= configModelDto.getMinSubCount() && maxSubCount <= configModelDto.getMaxSubCount())) {
                    poolLevel = configModelDto.getLevel();
                }
            }

            addressRecordModelDto.setPoolLevel(poolLevel);
            addressRecordService.update(addressRecordModelDto);

        }
    }

    public static void main(String[] args) {
        BigDecimal s = new BigDecimal(1);
        System.out.println(Math.pow(27.0, 1.0 / 3));
    }

}
