package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.PoolStatisticModel;
import com.liuqi.business.model.PoolStatisticModelDto;
import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;

public interface PoolStatisticService extends BaseService<PoolStatisticModel,PoolStatisticModelDto>{

    void addOrUpdateProfit(Long userId, Long currencyId, BigDecimal dailyProfit);

    void addOrUpdatePower(Long userId, Long currencyId, BigDecimal inviteComputePower);

    PoolStatisticModelDto getByUserIdAndCurrencyId(Long userId, Long currencyId);

    ReturnResponse getCoinList(LoginDto loginDto, Integer pageNum, Integer pageSize);

    void resetAll(Long currencyId);

}
