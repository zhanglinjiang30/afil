package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.PoolProfitRecordModel;
import com.liuqi.business.model.PoolProfitRecordModelDto;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PoolProfitRecordMapper extends BaseMapper<PoolProfitRecordModel,PoolProfitRecordModelDto>{

    PoolProfitRecordModelDto getByUserIdAndCurrencyIdAndDate(Long userId, Long currencyId, Date statisticDate);

}
