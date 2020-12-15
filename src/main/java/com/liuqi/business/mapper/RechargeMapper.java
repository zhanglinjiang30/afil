package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.CurrencyCountDto;
import com.liuqi.business.model.RechargeModel;
import com.liuqi.business.model.RechargeModelDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RechargeMapper extends BaseMapper<RechargeModel,RechargeModelDto>{


    List<CurrencyCountDto> queryCountByDate(@Param("date") Date date, @Param("currencyId") Long currencyId);
}
