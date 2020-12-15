package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.FinancingRecordModel;
import com.liuqi.business.model.FinancingRecordModelDto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface FinancingRecordMapper extends BaseMapper<FinancingRecordModel,FinancingRecordModelDto>{


    BigDecimal getConfigQuantity(Long configId);
}
