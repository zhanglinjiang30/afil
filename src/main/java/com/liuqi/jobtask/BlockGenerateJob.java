package com.liuqi.jobtask;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.business.service.BlockService;
import com.liuqi.business.service.TableIdService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.utils.DateTimeUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 区块生成job
 */
public class BlockGenerateJob implements Job {

    private static Log log = Log4j2LogFactory.get("autoTask");

    @Autowired
    private BlockService blockService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long i = RandomUtil.randomLong(1000L, 5000L);
        try {
            Thread.sleep(5000L + i);
        } catch (Exception e) {
        }

        RLock rLock = null;
        String key = "BlockGenerateJob";
        try {
            //一个去执行
            rLock = RedissonLockUtil.lock(key);
            blockService.generateBlock();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(rLock);
        }
    }


    //业务逻辑
    private void doIt() {
        System.out.println("------》" + DateTimeUtils.currentDateTime());
    }
}
