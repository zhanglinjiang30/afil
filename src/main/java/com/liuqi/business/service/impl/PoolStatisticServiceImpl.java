package com.liuqi.business.service.impl;


import com.github.pagehelper.PageInfo;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.*;
import com.liuqi.business.service.AddressHoldingRecordService;
import com.liuqi.business.service.CurrencyHoldingService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.service.PoolStatisticService;
import com.liuqi.business.mapper.PoolStatisticMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class PoolStatisticServiceImpl extends BaseServiceImpl<PoolStatisticModel,PoolStatisticModelDto> implements PoolStatisticService{

	@Autowired
	private PoolStatisticMapper poolStatisticMapper;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private CurrencyHoldingService currencyHoldingService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SearchPrice searchPrice;
	
	@Override
	public BaseMapper<PoolStatisticModel,PoolStatisticModelDto> getBaseMapper() {
		return this.poolStatisticMapper;
	}

	@Override
	protected void doMode(PoolStatisticModelDto dto) {
		CurrencyHoldingModelDto c = currencyHoldingService.getByCurrencyId(dto.getCurrencyId());
		CurrencyModelDto cc = currencyService.getById(dto.getCurrencyId(), false);
		dto.setCurrencyName(cc.getName());
		dto.setCurrencyImage(cc.getPic());
		dto.setMinHolding(c.getMinHolding());
		dto.setNiceHolding(c.getNiceHolding());
		dto.setPrice(MathUtil.mul(searchPrice.getPrice(dto.getCurrencyName()), searchPrice.getUsdtQcPrice()));
	}

	@Override
	@Transactional
	public void addOrUpdateProfit(Long userId, Long currencyId, BigDecimal dailyProfit) {
		PoolStatisticModelDto p = poolStatisticMapper.getByUserIdAndCurrencyId(userId, currencyId);
		if (p == null) {
			p = new PoolStatisticModelDto();
			p.setUserId(userId);
			p.setCurrencyId(currencyId);
			p.setDailyProfit(dailyProfit);
			p.setTotalProfit(dailyProfit);
			p.setComputePower(dailyProfit);
			p.setInviteComputePower(BigDecimal.ZERO);
			insert(p);
			return;
		}
		p.setComputePower(dailyProfit);
		p.setDailyProfit(dailyProfit);
		p.setTotalProfit(MathUtil.add(p.getTotalProfit(), dailyProfit));
		update(p);
	}

	@Override
	@Transactional
	public void addOrUpdatePower(Long userId, Long currencyId, BigDecimal inviteComputePower) {
		PoolStatisticModelDto p = poolStatisticMapper.getByUserIdAndCurrencyId(userId, currencyId);
		if (p == null) {
			p = new PoolStatisticModelDto();
			p.setUserId(userId);
			p.setCurrencyId(currencyId);
			p.setDailyProfit(BigDecimal.ZERO);
			p.setTotalProfit(BigDecimal.ZERO);
			p.setComputePower(BigDecimal.ZERO);
			p.setInviteComputePower(inviteComputePower);
			insert(p);
			return;
		}
		p.setInviteComputePower(MathUtil.add(p.getInviteComputePower(),inviteComputePower));
		update(p);
	}

	@Override
	public PoolStatisticModelDto getByUserIdAndCurrencyId(Long userId, Long currencyId) {
		return poolStatisticMapper.getByUserIdAndCurrencyId(userId, currencyId);
	}

	@Override
	public ReturnResponse getCoinList(LoginDto loginDto, Integer pageNum, Integer pageSize) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		PoolStatisticModelDto params = new PoolStatisticModelDto();
		params.setUserId(a.getAddressId());
		PageInfo<PoolStatisticModelDto> pageInfo = queryFrontPageByDto(params, pageNum, pageSize);
		return ReturnResponse.backSuccess(pageInfo);
	}

	@Override
	@Transactional
	public void resetAll(Long currencyId) {
		poolStatisticMapper.resetAll(currencyId);
	}
}
