package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.CurrencyHoldingModel;
import com.liuqi.business.model.CurrencyHoldingModelDto;
import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;

public interface CurrencyHoldingService extends BaseService<CurrencyHoldingModel,CurrencyHoldingModelDto>{

    void addOrUpdate(Long currencyId, BigDecimal minHolding, BigDecimal niceHolding, BigDecimal niceProfit);

    CurrencyHoldingModelDto getByCurrencyId(Long currencyId);

    /**
     * @Description 首页展示信息
     * @Date 15:14 2020/8/21
     */
    ReturnResponse getIndexInfo(LoginDto loginDto);
}
