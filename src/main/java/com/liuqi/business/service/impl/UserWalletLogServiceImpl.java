package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.mapper.UserWalletLogMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional(readOnly = true)
public class UserWalletLogServiceImpl extends BaseServiceImpl<UserWalletLogModel,UserWalletLogModelDto> implements UserWalletLogService{

	@Autowired
	private UserWalletLogMapper userWalletLogMapper;
	@Autowired
	private AddressRecordService addressRecordService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private TableIdService tableIdService;
	@Override
	public BaseMapper<UserWalletLogModel,UserWalletLogModelDto> getBaseMapper() {
		return this.userWalletLogMapper;
	}
	@Override
	public void insert(UserWalletLogModel userWalletLogModel) {
		//设置id
		userWalletLogModel.setId(tableIdService.getNextId(TableIdNameEnum.USER_WALLET_LOG));
		super.insert(userWalletLogModel);
	}

	@Override
	@Transactional
	public void addLog(Long userId, Long currencyId, BigDecimal money, Integer type,Long orderId, String remarks,UserWalletModel wallet) {
		UserWalletLogModel log = new UserWalletLogModel();
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
	public BigDecimal getMoneyByOrderId(Long orderId) {
		return userWalletLogMapper.getMoneyByOrderId(orderId);
	}

	@Override
	protected void doMode(UserWalletLogModelDto dto) {
		super.doMode(dto);
		dto.setUserName(addressRecordService.getById(dto.getUserId()).getAddress());
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}
