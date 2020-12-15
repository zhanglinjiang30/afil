package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.PoolStatisticModel;
import com.liuqi.business.model.PoolStatisticModelDto;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolStatisticMapper extends BaseMapper<PoolStatisticModel,PoolStatisticModelDto>{

    PoolStatisticModelDto getByUserIdAndCurrencyId(Long userId, Long currencyId);

    @Update("update t_pool_statistic set daily_profit = 0, compute_power = 0, invite_compute_power = 0 where currency_id = #{currencyId}")
    void resetAll(Long currencyId);

}
