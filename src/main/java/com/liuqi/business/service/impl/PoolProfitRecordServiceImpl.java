package com.liuqi.business.service.impl;


import com.github.pagehelper.PageInfo;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.dto.MiningProfitRateDto;
import com.liuqi.business.dto.MiningTotalProfitDto;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.DateUtil;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.PoolProfitRecordMapper;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PoolProfitRecordServiceImpl extends BaseServiceImpl<PoolProfitRecordModel,PoolProfitRecordModelDto> implements PoolProfitRecordService{

	@Autowired
	private PoolProfitRecordMapper poolProfitRecordMapper;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private PoolStatisticService poolStatisticService;
	@Autowired
	private CurrencyHoldingService currencyHoldingService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SearchPrice searchPrice;

	@Override
	public BaseMapper<PoolProfitRecordModel,PoolProfitRecordModelDto> getBaseMapper() {
		return this.poolProfitRecordMapper;
	}

	@Override
	protected void doMode(PoolProfitRecordModelDto dto) {
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	@Transactional
	public void addStaticRecord(Long userId, Long currencyId, BigDecimal staticProfit, BigDecimal profitRate) {
		Date statisticDate = DateUtil.getDay(DateUtil.getDateAddDays(new Date(), -1));
		PoolProfitRecordModelDto record = poolProfitRecordMapper.getByUserIdAndCurrencyIdAndDate(userId, currencyId, statisticDate);
		if (record != null) {
			record.setStaticProfit(staticProfit);
			record.setProfitRate(profitRate);
			update(record);
			return;
		}
		record = new PoolProfitRecordModelDto();
		record.setUserId(userId);
		record.setCurrencyId(currencyId);
		record.setStaticProfit(staticProfit);
		record.setDynamicProfit(BigDecimal.ZERO);
		record.setProfitRate(profitRate);
		record.setStatisticDate(statisticDate);
		insert(record);
	}

	@Override
	@Transactional
	public void addDynamicRecord(Long userId, Long currencyId, BigDecimal dynamicProfit) {
		Date statisticDate = DateUtil.getDay(DateUtil.getDateAddDays(new Date(), -1));
		PoolProfitRecordModelDto record = poolProfitRecordMapper.getByUserIdAndCurrencyIdAndDate(userId, currencyId, statisticDate);
		if (record != null) {
			record.setDynamicProfit(MathUtil.add(record.getDynamicProfit(),dynamicProfit));
			update(record);
			return;
		}
		record = new PoolProfitRecordModelDto();
		record.setUserId(userId);
		record.setCurrencyId(currencyId);
		record.setStaticProfit(BigDecimal.ZERO);
		record.setProfitRate(BigDecimal.ZERO);
		record.setDynamicProfit(dynamicProfit);
		record.setStatisticDate(statisticDate);
		insert(record);
	}

	@Override
	public ReturnResponse getProfitSummary(LoginDto loginDto, Long currencyId) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		Map result = new HashMap();
		PoolProfitRecordModelDto params = new PoolProfitRecordModelDto();
		params.setUserId(a.getAddressId());
		params.setCurrencyId(currencyId);
		params.setSortName("statistic_date");
		params.setSortType(" desc limit 1");
		List<PoolProfitRecordModelDto> list = queryListByDto(params, false);
		CurrencyModelDto currencyModelDto = currencyService.getById(currencyId);
		result.put("currencyName", currencyModelDto.getName());
		// 日期及收益
		BigDecimal profit = list.get(0).getStaticProfit().add(list.get(0).getDynamicProfit());
		result.put("date", DateUtil.formatDate(list.get(0).getStatisticDate(), "MM-dd"));
		result.put("profit", profit);
		result.put("money", MathUtil.mul(MathUtil.mul(searchPrice.getPrice(currencyModelDto.getName()), profit), searchPrice.getUsdtQcPrice()));
		// 持币算力和推广算力
		PoolStatisticModelDto p = poolStatisticService.getByUserIdAndCurrencyId(a.getAddressId(), currencyId);
		result.put("computePower", p.getComputePower());
		result.put("inviteComputePower", p.getInviteComputePower());
		// 最低持币、最佳持币、最佳持币收益
		CurrencyHoldingModelDto c = currencyHoldingService.getByCurrencyId(currencyId);
		result.put("minHolding", c.getMinHolding());
		result.put("niceHolding", c.getNiceHolding());
		result.put("niceProfit", c.getNiceProfit());
		// 最近7天收益
		params.setUserId(a.getAddressId());
		params.setCurrencyId(currencyId);
		params.setSortName("statistic_date");
		params.setSortType(" desc limit 7");
		list = queryListByDto(params, false);
		//
		List<MiningTotalProfitDto> allProfitList = new ArrayList<>();
		List<MiningProfitRateDto> profitRateList = new ArrayList<>();
		Collections.sort(list, Comparator.comparing(PoolProfitRecordModelDto::getStatisticDate));
		list.forEach(m -> {
			allProfitList.add(MiningTotalProfitDto.builder().date(m.getStatisticDate()).totalProfit(MathUtil.add(m.getStaticProfit(), m.getDynamicProfit())).build());
			profitRateList.add(MiningProfitRateDto.builder().date(m.getStatisticDate()).profitRate(m.getProfitRate()).build());
		});
		// 最近7天收益
		result.put("weekProfit", allProfitList);
		// 最近7天收益率
		result.put("profitRate", profitRateList);
		result.put("address", a.getAddress());
		return ReturnResponse.backSuccess(result);
	}

	@Override
	public ReturnResponse getProfitList(LoginDto loginDto, Long currencyId, Integer pageNum, Integer pageSize) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		PoolProfitRecordModelDto params = new PoolProfitRecordModelDto();
		params.setUserId(a.getAddressId());
		params.setCurrencyId(currencyId);
		PageInfo<PoolProfitRecordModelDto> pageInfo = queryFrontPageByDto(params, pageNum, pageSize);
		return ReturnResponse.backSuccess(pageInfo);
	}
}
