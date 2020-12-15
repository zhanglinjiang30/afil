package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CrowdfundInfoModel;
import com.liuqi.business.model.CrowdfundInfoModelDto;
import org.apache.ibatis.annotations.Param;


public interface CrowdfundInfoMapper extends BaseMapper<CrowdfundInfoModel, CrowdfundInfoModelDto>{


    CrowdfundInfoModelDto getEnableInfoByFundId(@Param("crowdfundId") Long crowdfundId);

    CrowdfundInfoModelDto getByIndex(@Param("index") Integer index, @Param("crowdfundId") Long crowdfundId);

    CrowdfundInfoModelDto getLastInfoByFundId(Long fundId);
}
