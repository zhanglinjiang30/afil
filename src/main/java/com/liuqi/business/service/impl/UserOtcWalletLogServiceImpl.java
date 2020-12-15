package com.liuqi.business.service.impl;


import com.liuqi.business.model.*;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.TableIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.service.UserOtcWalletLogService;
import com.liuqi.business.mapper.UserOtcWalletLogMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class UserOtcWalletLogServiceImpl extends BaseServiceImpl<UserOtcWalletLogModel,UserOtcWalletLogModelDto> implements UserOtcWalletLogService{

	@Autowired
	private UserOtcWalletLogMapper userOtcWalletLogMapper;
	

	@Override
	public BaseMapper<UserOtcWalletLogModel,UserOtcWalletLogModelDto> getBaseMapper() {
		return this.userOtcWalletLogMapper;
	}

	@Autowired
	private AddressRecordService addressRecordService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private TableIdService tableIdService;

	@Override
	public void insert(UserOtcWalletLogModel userOtcWalletLogModel) {
		super.insert(userOtcWalletLogModel);
	}

	@Override
	@Transactional
	public void addLog(Long userId, Long currencyId, BigDecimal money, Integer type, Long orderId,
					   String remarks, UserOtcWalletModel wallet) {
		UserOtcWalletLogModel log = new UserOtcWalletLogModel();
		log.setCurrencyId(currencyId);
		log.setMoney(money);
		log.setUserId(userId);
		log.setType(type);
		log.setRemark(remarks);
		log.setOrderId(orderId);
		log.setBalance(wallet.getUsing());
		log.setSnapshot("可用："+wallet.getUsing()+",冻结："+wallet.getFreeze());
		this.insert(log);
	}


	@Override
	protected void doMode(UserOtcWalletLogModelDto dto) {
		super.doMode(dto);
		dto.setUserName(addressRecordService.getById(dto.getUserId()).getAddress());
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}
