package com.liuqi.business.service.impl;


import com.github.pagehelper.PageInfo;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.ComputePowerRecordMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class ComputePowerRecordServiceImpl extends BaseServiceImpl<ComputePowerRecordModel,ComputePowerRecordModelDto> implements ComputePowerRecordService{

	@Autowired
	private ComputePowerRecordMapper computePowerRecordMapper;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private AddressRecordService addressRecordService;
	@Autowired
	private UserWalletService userWalletService;

	@Autowired
	private UserPoolWalletService userPoolWalletService;

	@Override
	protected void doMode(ComputePowerRecordModelDto dto) {
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setAddress(addressRecordService.getById(dto.getUserId(), false).getAddress());
		UserPoolWalletModel u = userPoolWalletService.getByUserAndCurrencyId(dto.getUserId(), dto.getCurrencyId());
		dto.setHolding(u.getUsing());
	}

	@Override
	public BaseMapper<ComputePowerRecordModel,ComputePowerRecordModelDto> getBaseMapper() {
		return this.computePowerRecordMapper;
	}

	@Override
	@Transactional
	public void  add(Long userId, Long pUserId, Long currencyId, BigDecimal power, Integer largeZone) {
		ComputePowerRecordModelDto c = computePowerRecordMapper.getByUserIdAndCurrencyId(userId, currencyId);
		if (c != null) {
			c.setPUserId(pUserId);
			c.setPower(MathUtil.add(c.getPower(),power));
			c.setLargeZone(largeZone);
			update(c);
			return;
		}
		c = new ComputePowerRecordModelDto();
		c.setUserId(userId);
		c.setPUserId(pUserId);
		c.setCurrencyId(currencyId);
		c.setPower(power);
		c.setLargeZone(largeZone);
		insert(c);
	}

	@Override
	public ReturnResponse getInviteList(LoginDto loginDto, Long currencyId, Integer pageNum, Integer pageSize) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		ComputePowerRecordModelDto params = new ComputePowerRecordModelDto();
		params.setPUserId(a.getAddressId());
		params.setCurrencyId(currencyId);
		params.setSortName("large_zone");
		params.setSortType(" desc");
		PageInfo<ComputePowerRecordModelDto> pageInfo =  queryFrontPageByDto(params, pageNum, pageSize);
		return ReturnResponse.backSuccess(pageInfo);
	}
}
