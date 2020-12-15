package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.CoinHoldProfitHandleService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateTimeUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 挖矿结算  【每日00:00:01执行】
 */
public class PoolProfitJob implements Job {

    private static Log log = Log4j2LogFactory.get("autoTask");

    @Autowired
    private CoinHoldProfitHandleService coinHoldProfitHandleService;
    @Autowired
    private CurrencyService currencyService;


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("矿池收益开始");
        List<CurrencyModelDto> list = currencyService.getAll();
        list.forEach(m -> coinHoldProfitHandleService.doMining(m.getId()));
        log.info("矿池收益结束");
    }


    //业务逻辑
    private void doIt(){
        System.out.println("------》"+DateTimeUtils.currentDateTime());
    }
}
