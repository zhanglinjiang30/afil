package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.service.CrowdfundInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class CrowdfundProfitJob implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");

    @Autowired
    private CrowdfundInfoService crowdfundInfoService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("投票收益定时任务开始");

        crowdfundInfoService.profit();

        log.info("投票收益定时任务完成");
    }

    private void doIt(){
        crowdfundInfoService.profit();
    }
}
