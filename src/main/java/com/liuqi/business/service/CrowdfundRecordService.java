package com.liuqi.business.service;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CrowdfundRecordModel;
import com.liuqi.business.model.CrowdfundRecordModelDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CrowdfundRecordService extends BaseService<CrowdfundRecordModel, CrowdfundRecordModelDto>{

    List<CrowdfundRecordModelDto> getByAddressIdAndInfoId(Long addressId,Long fundInfoId);

    BigDecimal getTotalByAddressId(Long addressId, Long fundInfoId);

    void addRecord(Long addressId,Long fundInfoId,BigDecimal amount,Long currencyId,
                   BigDecimal gainAmount,Long gainCurrencyId,BigDecimal unitPrice);

    List<Map<String,Object>> getSummaryByInfoId(Long infoId);

    PageInfo<CrowdfundRecordModelDto> getRecordList(Integer pageNum,Integer pageSize,CrowdfundRecordModelDto crowdfundRecordModelDto);

    Map<String,Object> getTeamCount(Long addressId);

    PageInfo<Map<String,Object>> getTeamList(Long addressId,Long currencyId,Integer pageNum,Integer pageSize);
}
