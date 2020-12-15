package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CrowdfundInfoModel;
import com.liuqi.business.model.CrowdfundInfoModelDto;

import java.math.BigDecimal;

public interface CrowdfundInfoService extends BaseService<CrowdfundInfoModel, CrowdfundInfoModelDto>{

    void buy(Long addressId, Long infoId, BigDecimal amount);

    CrowdfundInfoModelDto getEnableInfoByFundId(Long crowdfundId);

    void profit();


    void release();

    CrowdfundInfoModelDto getLastInfoByFundId(Long fundId);
}
