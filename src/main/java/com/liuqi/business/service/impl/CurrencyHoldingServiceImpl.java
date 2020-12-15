package com.liuqi.business.service.impl;


import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.*;
import com.liuqi.business.service.AddressHoldingRecordService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.PoolStatisticService;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import com.sun.mail.imap.protocol.SearchSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.service.CurrencyHoldingService;
import com.liuqi.business.mapper.CurrencyHoldingMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CurrencyHoldingServiceImpl extends BaseServiceImpl<CurrencyHoldingModel,CurrencyHoldingModelDto> implements CurrencyHoldingService{

	@Autowired
	private CurrencyHoldingMapper currencyHoldingMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private PoolStatisticService poolStatisticService;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private SearchPrice searchPrice;

	@Override
	public BaseMapper<CurrencyHoldingModel,CurrencyHoldingModelDto> getBaseMapper() {
		return this.currencyHoldingMapper;
	}

	@Override
	@Transactional
	public void addOrUpdate(Long currencyId, BigDecimal minHolding, BigDecimal niceHolding, BigDecimal niceProfit) {
		CurrencyHoldingModelDto c = currencyHoldingMapper.getByCurrencyId(currencyId);
		if (c == null) {
			c = new CurrencyHoldingModelDto();
			c.setCurrencyId(currencyId);
			c.setMinHolding(minHolding);
			c.setNiceHolding(niceHolding);
			c.setNiceProfit(niceProfit);
			insert(c);
			return;
		}
		c.setMinHolding(minHolding);
		c.setNiceHolding(niceHolding);
		c.setNiceProfit(niceProfit);
		update(c);
	}

	@Override
	public CurrencyHoldingModelDto getByCurrencyId(Long currencyId) {
		return currencyHoldingMapper.getByCurrencyId(currencyId);
	}

	@Override
	public ReturnResponse getIndexInfo(LoginDto loginDto) {
		Map result = new HashMap();
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		Long sucId = currencyService.getPTId();
		PoolStatisticModelDto poolStatisticModelDto = poolStatisticService.getByUserIdAndCurrencyId(a.getAddressId(), sucId);
		BigDecimal todayProfit = poolStatisticModelDto == null ? BigDecimal.ZERO : poolStatisticModelDto.getDailyProfit();
		// 今日收益
		result.put("todayProfit", todayProfit);
		BigDecimal sucPrice = searchPrice.getPrice("AFIL");
		sucPrice = MathUtil.mul(sucPrice, searchPrice.getUsdtQcPrice());
		sucPrice = MathUtil.mul(sucPrice, todayProfit);
		// CNY数量
		result.put("sucMoney", sucPrice);
		CurrencyHoldingModelDto c = currencyHoldingMapper.getByCurrencyId(sucId);
		// 最低持币
		result.put("minHolding", c == null ? BigDecimal.ZERO : c.getMinHolding());
		// 最佳持币
		result.put("niceHolding", c == null ? BigDecimal.ZERO : c.getNiceHolding());
		return ReturnResponse.backSuccess(result);
	}
}
