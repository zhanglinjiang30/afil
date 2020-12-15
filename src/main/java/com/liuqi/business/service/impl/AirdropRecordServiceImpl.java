package com.liuqi.business.service.impl;


import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import com.liuqi.utils.ShiroPasswdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.AirdropRecordMapper;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
@Transactional(readOnly = true)
public class AirdropRecordServiceImpl extends BaseServiceImpl<AirdropRecordModel,AirdropRecordModelDto> implements AirdropRecordService{

	@Autowired
	private AirdropRecordMapper airdropRecordMapper;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SearchPrice searchPrice;
	@Autowired
	private AddressRecordService addressRecordService;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	

	@Override
	public BaseMapper<AirdropRecordModel,AirdropRecordModelDto> getBaseMapper() {
		return this.airdropRecordMapper;
	}

	@Override
	protected void doMode(AirdropRecordModelDto dto) {
		dto.setSucAddress(addressRecordService.getById(dto.getAddressId(), false).getAddress());
	}

	@Override
	@Transactional
	public ReturnResponse doAirDrop(LoginDto loginDto, String tradePwd, BigDecimal sucAmount) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		if (!StringUtils.equals(addressHoldingRecordService.getMainAddress(loginDto).getTradePwd(), ShiroPasswdUtil.getUserPwd(tradePwd))) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("errorMessage5"));
		}
		Long sucId = currencyService.getByName("AFIL").getId();
		Long usdtId = currencyService.getByName("USDT").getId();

		BigDecimal sucPrice = searchPrice.getPrice("AFIL");
		BigDecimal usdtQuantity = MathUtil.mul(sucAmount, sucPrice);
		UserWalletModelDto usdtWallet = userWalletService.getByUserAndCurrencyId(a.getAddressId(), usdtId);
		if (usdtWallet.getUsing().compareTo(usdtQuantity) < 0) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message54"));
		}

		UserWalletModelDto u1 = userWalletService.modifyWalletUsing(a.getAddressId(), usdtId, MathUtil.zeroSub(usdtQuantity));
		UserWalletModelDto u2 = userWalletService.modifyWalletUsing(a.getAddressId(), sucId, sucAmount);

		AirdropRecordModel airdropRecordModel = new AirdropRecordModel();
		airdropRecordModel.setAddressId(a.getAddressId());
		airdropRecordModel.setUsdtAmount(usdtQuantity);
		airdropRecordModel.setSucAmount(sucAmount);
		insert(airdropRecordModel);

		userWalletLogService.addLog(a.getAddressId(), usdtId, MathUtil.zeroSub(usdtQuantity), WalletLogTypeEnum.AIR_DROP_SUB_USDT.getCode(), airdropRecordModel.getId(), WalletLogTypeEnum.AIR_DROP_SUB_USDT.getName(), u1);
		userWalletLogService.addLog(a.getAddressId(), sucId, sucAmount, WalletLogTypeEnum.AIR_DROP_ADD_SUC.getCode(), airdropRecordModel.getId(), WalletLogTypeEnum.AIR_DROP_ADD_SUC.getName(), u2);

		addressRecordService.updateAirDropAmount(Arrays.asList(new Long[] {a.getAddressId()}), BigDecimal.ZERO);
		return ReturnResponse.backSuccess();
	}
}
