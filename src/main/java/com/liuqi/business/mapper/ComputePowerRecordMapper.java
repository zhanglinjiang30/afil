package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.ComputePowerRecordModel;
import com.liuqi.business.model.ComputePowerRecordModelDto;
import org.springframework.stereotype.Repository;

@Repository
public interface ComputePowerRecordMapper extends BaseMapper<ComputePowerRecordModel,ComputePowerRecordModelDto>{

    ComputePowerRecordModelDto getByUserIdAndCurrencyId(Long userId, Long currencyId);

}
