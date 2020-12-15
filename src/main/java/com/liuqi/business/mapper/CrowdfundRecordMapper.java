package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CrowdfundRecordModel;
import com.liuqi.business.model.CrowdfundRecordModelDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface CrowdfundRecordMapper extends BaseMapper<CrowdfundRecordModel, CrowdfundRecordModelDto>{


    List<CrowdfundRecordModelDto> getByAddressIdAndInfoId(@Param("addressId") Long addressId,
                                                          @Param("fundInfoId") Long fundInfoId);

    List<Map<String,Object>> getSummaryByInfoId(Long infoId);

    List<CrowdfundRecordModelDto> getRecordList(CrowdfundRecordModelDto crowdfundRecordModelDto);

    List<Integer> getValidCountByUserIds(List<Long> userIds);

    BigDecimal getQuantityByAddressId(@Param("addressId") Long addressId, @Param("targetCurrencyId") Long targetCurrencyId);
}
