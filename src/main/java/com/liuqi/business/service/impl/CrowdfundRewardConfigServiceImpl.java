package com.liuqi.business.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.CrowdfundRewardConfigModel;
import com.liuqi.business.model.CrowdfundRewardConfigModelDto;


import com.liuqi.business.service.CrowdfundRewardConfigService;
import com.liuqi.business.mapper.CrowdfundRewardConfigMapper;

@Service
@Transactional(readOnly = true)
public class CrowdfundRewardConfigServiceImpl extends BaseServiceImpl<CrowdfundRewardConfigModel, CrowdfundRewardConfigModelDto> implements CrowdfundRewardConfigService {

	@Autowired
	private CrowdfundRewardConfigMapper crowdfundRewardConfigMapper;
	

	@Override
	public BaseMapper<CrowdfundRewardConfigModel, CrowdfundRewardConfigModelDto> getBaseMapper() {
		return this.crowdfundRewardConfigMapper;
	}

}
