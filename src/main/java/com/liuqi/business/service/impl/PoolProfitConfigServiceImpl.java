package com.liuqi.business.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.PoolProfitConfigModel;
import com.liuqi.business.model.PoolProfitConfigModelDto;


import com.liuqi.business.service.PoolProfitConfigService;
import com.liuqi.business.mapper.PoolProfitConfigMapper;

@Service
@Transactional(readOnly = true)
public class PoolProfitConfigServiceImpl extends BaseServiceImpl<PoolProfitConfigModel,PoolProfitConfigModelDto> implements PoolProfitConfigService{

	@Autowired
	private PoolProfitConfigMapper poolProfitConfigMapper;



	@Override
	public BaseMapper<PoolProfitConfigModel,PoolProfitConfigModelDto> getBaseMapper() {
		return this.poolProfitConfigMapper;
	}


	@Override
	protected void doMode(PoolProfitConfigModelDto dto) {
		super.doMode(dto);
	}
}
