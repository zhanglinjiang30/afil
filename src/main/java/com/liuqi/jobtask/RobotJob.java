package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 机器人发布
 */
public class RobotJob implements Job {

    @Autowired
    private RobotService robotService;
    @Autowired
    private TrusteeService trusteeService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeRecordService tradeRecordService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private SearchPrice searchPrice;
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    private static Log log = Log4j2LogFactory.get("robot");

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Long id = jobExecutionContext.getJobDetail().getJobDataMap().getLong("id");
        RobotModel robotModel = robotService.getById(id);
        if (SwitchEnum.isOn(robotModel.getBuySwitch())) {
            this.robotPublish(robotModel, BuySellEnum.BUY.getCode());
        }
        if (SwitchEnum.isOn(robotModel.getSellSwitch())) {
            this.robotPublish(robotModel, BuySellEnum.SELL.getCode());
        }
    }


    private void robotPublish(RobotModel robotModel, Integer tradeType) {
        try {
            CurrencyTradeModelDto trade = currencyTradeService.getById(robotModel.getTradeId());
            String tradeName=trade.getTradeCurrencyName()+"/"+trade.getCurrencyName();
            if(SwitchEnum.OFF.getCode().equals(trade.getTradeSwitch())){
                log.info(tradeName+",未开放交易");
                return;
            }
            //1.1获取价格
            BigDecimal price = this.getPrice(robotModel,trade);
            if(price.compareTo(BigDecimal.ZERO)<=0){
                log.info(tradeName+"未获取到价格");
                throw new BusinessException(tradeName+"未获取到价格");
            }

            //1.2 获取交易量
            BigDecimal quantity=this.getQuantity(robotModel);
            //0真实交易  1虚拟交易
            if (robotModel.getRunType().equals(RobotModelDto.RUNTYPE_VIRTUAL)) {
                TradeRecordModel record = TradeRecordModelDto.buildRobotTradeRecordModel(robotModel.getTradeId(), quantity, price, tradeType);
                tradeRecordService.insert(record);
            }else {
                TrusteeModelDto trusteeModel = new TrusteeModelDto();
                trusteeModel.setPrice(price);
                //根据类型 取消买卖单子
                trusteeService.cancelRobot(robotModel.getTradeId(), tradeType, price);

                trusteeModel.setUserId(robotModel.getUserId());
                trusteeModel.setPriority(1);//设置用户交易优先级
                trusteeModel.setTradeId(robotModel.getTradeId());//交易对id
                trusteeModel.setQuantity(quantity);//交易量
                trusteeModel.setTradeType(tradeType);//交易类型 买/卖
                //是否操作钱包开关 开
                if(SwitchEnum.isOn(robotModel.getWalletSwitch())) {
                    trusteeModel.setRobot(YesNoEnum.NO.getCode());//机器人
                }else{
                    trusteeModel.setRobot(YesNoEnum.YES.getCode());//机器人
                }
                //检查发布
                trusteeService.checkPublish(trusteeModel);
                //发布
                trusteeService.publishTrade(trusteeModel);

                //发布缓存修改
                tradeInfoCacheService.publishCache(trusteeModel.getTradeId(), trusteeModel.getTradeType(), trusteeModel.getPrice(), trusteeModel.getQuantity(), YesNoEnum.YES.getCode().equals(trusteeModel.getRobot()));

            }
            //不等于0 清除
            if(StringUtils.isNotEmpty(robotModel.getRemark())) {
                robotModel.setRemark("");
                robotService.update(robotModel);
            }
        } catch (Exception e) {
            robotModel.setRemark(e.getMessage());
            robotService.update(robotModel);
            e.printStackTrace();
        }
    }


    private BigDecimal getPrice(RobotModel robotModel, CurrencyTradeModelDto trade ){
        BigDecimal price = BigDecimal.ZERO;

        //在线价格  直接返回价格
        if (robotModel.getType().equals(RobotModelDto.TYPE_OUTER)) {
            if (StringUtils.equals(trade.getTradeCurrencyName(), "AFIL")) {
                price = MathUtil.div(BigDecimal.ONE, searchPrice.getUsdtQcPrice());
            } else if (StringUtils.isNotEmpty(trade.getSearchName())){
                price = searchPrice.getPrice(trade.getSearchName());
            }
        }else{
            //开盘价
            BigDecimal openPrice = tradeService.getOpenPrice(trade.getId());

            //获取卖1价格
            price = trusteeService.findTrusteeSellMinPrice(trade.getId());
            //没有价格  获取开盘价
            if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
                //获取当前价
                price = tradeService.getPriceByTradeId(trade.getId());
                //当前没有价格  获取开盘价
                if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
                    price = openPrice;
                }
            }
            /**
             * 买卖单都判断涨幅
             *   1获取机器人设置的涨幅
             *   2获取交易对设置的涨幅（未设置默认为50%）
             *   3获取小的那个涨幅
             *   4价格限制在涨幅之间
             *
             *
             * 1机器人没设置涨幅，交易对没设置涨幅   默认取50%
             * 2机器人没设置涨幅，交易对设置涨幅     获取交易对涨幅
             * 3机器人设置涨幅，交易对设置涨幅       获取小的那个
             */
            //判断是否设置涨跌幅
            BigDecimal curRate = robotModel.getRate().compareTo(BigDecimal.ZERO) > 0 ? MathUtil.div(robotModel.getRate(), new BigDecimal("100")) : BigDecimal.ZERO;
            //获取总涨跌幅  默认0.5
            BigDecimal limitRate = SwitchEnum.ON.getCode().equals(trade.getTradeSwitch()) && trade.getLimitRate().compareTo(BigDecimal.ZERO) > 0 ? MathUtil.div(trade.getLimitRate(), new BigDecimal("100")) : new BigDecimal("0.5");
            //如果当前设置的涨跌幅小于总涨跌幅  则按照当前设置涨跌幅设置
            if (curRate.compareTo(BigDecimal.ZERO) > 0 && curRate.compareTo(limitRate) < 0) {
                limitRate = curRate;
            }

            //涨幅
            BigDecimal rate = openPrice.multiply(limitRate);
            BigDecimal maxPrice = openPrice.add(rate);//最大价格  当前价格+涨跌幅
            BigDecimal minPrice = openPrice.subtract(rate);//最大价格  当前价格-涨跌幅

            //涨
            if (robotModel.getUpDown().equals(RobotModelDto.UP)) {
                price = MathUtil.add(price, robotModel.getIntervalPrice());
                //超过最大值  最大值
                if (price.compareTo(maxPrice) > 0) {
                    price = maxPrice;
                }
            } else {
                price = MathUtil.sub(price, robotModel.getIntervalPrice());
                if (price.compareTo(minPrice) < 0) {
                    price = minPrice;
                }
            }
        }
        if(price.compareTo(BigDecimal.ZERO)<=0){
            price=BigDecimal.ZERO;
        }
        return price;
    }

    private BigDecimal getQuantity(RobotModel robotModel){
        BigDecimal minQuantity = robotModel.getMinQuantity();//最大交易量
        BigDecimal maxQuantityMin = robotModel.getMaxQuantity();//最小交易量
        //随机数*（最大值-最小值）+最小值
        Double v = Math.random() * (maxQuantityMin.doubleValue() - minQuantity.doubleValue()) + minQuantity.doubleValue();//随机数
        return  new BigDecimal(v).setScale(6, BigDecimal.ROUND_DOWN);
    }
}
