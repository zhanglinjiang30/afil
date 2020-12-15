package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CurrencyHoldingModel;
import com.liuqi.business.model.CurrencyHoldingModelDto;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyHoldingMapper extends BaseMapper<CurrencyHoldingModel,CurrencyHoldingModelDto>{

    CurrencyHoldingModelDto getByCurrencyId(Long currencyId);

}
