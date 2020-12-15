package com.liuqi.jobtask;

import com.liuqi.business.dto.CoinDogArticle;
import com.liuqi.business.model.CoinArticleModelDto;
import com.liuqi.business.service.CoinArticleService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 精选 信息 获取任务
 */
public class ImportantInfoJob implements Job {

    @Autowired
    private CoinArticleService coinArticleService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.doIt();
    }

    //业务逻辑
    private void doIt() {
        String lastId = "";
        // 获取最后一条记录的id
        CoinArticleModelDto lastRecord = coinArticleService.getLastId();
        lastId = lastRecord == null ? "725555" : lastRecord.getAid().toString();
        //查询数据库最新获取的快讯 ID

        List<CoinArticleModelDto> coinArticle = CoinDogArticle.getCoinArticle(lastId);
        if (coinArticle == null) {
            return;
        }

        for (CoinArticleModelDto coinArticleDto : coinArticle) {
            CoinArticleModelDto articleModel = coinArticleService.getByAid(coinArticleDto.getId());
            if (articleModel == null) {
                coinArticleDto.setAid(coinArticleDto.getId());
                coinArticleService.insert(coinArticleDto);
            }

        }

    }
}
