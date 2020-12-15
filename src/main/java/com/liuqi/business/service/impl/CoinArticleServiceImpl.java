package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.CoinArticleMapper;
import com.liuqi.business.model.CoinArticleModel;
import com.liuqi.business.model.CoinArticleModelDto;
import com.liuqi.business.service.CoinArticleService;
import com.liuqi.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CoinArticleServiceImpl extends BaseServiceImpl<CoinArticleModel, CoinArticleModelDto> implements CoinArticleService {

	@Autowired
	private CoinArticleMapper coinArticleMapper;
	

	@Override
	public BaseMapper<CoinArticleModel, CoinArticleModelDto> getBaseMapper() {
		return this.coinArticleMapper;
	}

	@Override
	protected void doMode(CoinArticleModelDto dto) {
		dto.setTimeString(DateUtil.formatDate(dto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
	}

	@Override
	public CoinArticleModelDto getLastId() {
		return coinArticleMapper.getLastRecord();
	}

	@Override
	public CoinArticleModelDto getByAid(Long id) {
		return coinArticleMapper.getByAid(id);
	}
}
