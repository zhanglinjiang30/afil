package com.liuqi.business.service;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CrowdfundModel;
import com.liuqi.business.model.CrowdfundModelDto;

public interface CrowdfundService extends BaseService<CrowdfundModel,CrowdfundModelDto>{

    PageInfo<CrowdfundModelDto> getCrowdfundList(Integer pageNum,Integer pageSize,CrowdfundModelDto crowdfundModelDto);
}
