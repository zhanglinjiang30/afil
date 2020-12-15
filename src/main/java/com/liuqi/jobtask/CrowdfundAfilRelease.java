package com.liuqi.jobtask;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.service.CrowdfundInfoService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateTimeUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 例子
 */
public class CrowdfundAfilRelease implements Job {
    private static Log log = Log4j2LogFactory.get("autoTask");

    @Autowired
    private CrowdfundInfoService crowdfundInfoService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("投票AFIL释放开始");

        this.doIt();
        log.info("投票AFIL释放结束");

    }


    //业务逻辑
    private void doIt() {
        crowdfundInfoService.release();
    }
}
