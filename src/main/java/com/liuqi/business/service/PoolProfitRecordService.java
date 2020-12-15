package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.PoolProfitRecordModel;
import com.liuqi.business.model.PoolProfitRecordModelDto;
import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;

public interface PoolProfitRecordService extends BaseService<PoolProfitRecordModel,PoolProfitRecordModelDto>{

    void addStaticRecord(Long userId, Long currencyId, BigDecimal staticProfit, BigDecimal profitRate);

    void addDynamicRecord(Long userId, Long currencyId, BigDecimal dynamicProfit);

    ReturnResponse getProfitSummary(LoginDto loginDto, Long currencyId);

    ReturnResponse getProfitList(LoginDto loginDto, Long currencyId, Integer pageNum, Integer pageSize);
}
