package com.liuqi.business.service.impl;


import com.liuqi.business.enums.UserWalletTypeEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.enums.WalletTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.WalletTransferMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class WalletTransferServiceImpl extends BaseServiceImpl<WalletTransferModel,WalletTransferModelDto> implements WalletTransferService{

	@Autowired
	private WalletTransferMapper walletTransferMapper;
	

	@Override
	public BaseMapper<WalletTransferModel,WalletTransferModelDto> getBaseMapper() {
		return this.walletTransferMapper;
	}


	@Autowired
	private UserWalletService userWalletService;

	@Autowired
	private UserPoolWalletService userPoolWalletService;

	@Autowired
	private UserOtcWalletService userOtcWalletService;

	@Autowired
	private UserWalletLogService userWalletLogService;

	@Autowired
	private UserPoolWalletLogService userPoolWalletLogService;

	@Autowired
	private UserOtcWalletLogService userOtcWalletLogService;

	@Autowired
	private CurrencyService currencyService;

	@Override
	protected void doMode(WalletTransferModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Transactional
	public Long addTransfer(Long userId,Long currencyId,BigDecimal quantity,Integer source,Integer target){
		WalletTransferModel transfer = new WalletTransferModel();
		transfer.setUserId(userId);
		transfer.setCurrencyId(currencyId);
		transfer.setQuantity(quantity);
		transfer.setSource(source);
		transfer.setTarget(target);
		transfer.setStatus(1);
		this.insert(transfer);
		return transfer.getId();
	}

	private void checkCoinWallet(Long userId,Long currencyId,BigDecimal quantity){
		//币币账户
		UserWalletModelDto coinDto = userWalletService.getByUserAndCurrencyId(userId,currencyId);

		if (coinDto.getUsing().compareTo(quantity)<0){
			throw new BusinessException(MessageSourceHolder.getMessage("message11") + coinDto.getUsing());
		}
	}

	private void checkOtcWallet(Long userId,Long currencyId,BigDecimal quantity){
		//法币账户
		UserOtcWalletModelDto otcDto = userOtcWalletService.getByUserAndCurrencyId(userId,currencyId);

		if (otcDto.getUsing().compareTo(quantity)<0){
			throw new BusinessException(MessageSourceHolder.getMessage("message11") + otcDto.getUsing());
		}
	}

	private void checkPoolWallet(Long userId,Long currencyId,BigDecimal quantity){
		//矿池账户
		UserPoolWalletModelDto poolDto = userPoolWalletService.getByUserAndCurrencyId(userId,currencyId);

		if (poolDto.getUsing().compareTo(quantity)<0){
			throw new BusinessException(MessageSourceHolder.getMessage("message11") + poolDto.getUsing());
		}
	}
	/**
	 * 币币账户转法币
	 */
	@Transactional
	public void coinTransferOtc(Long userId, Long currencyId, BigDecimal quantity){
		//币币账户
		this.checkCoinWallet(userId,currencyId,quantity);

		Long transferId = this.addTransfer(userId,currencyId,quantity, UserWalletTypeEnum.USING.getCode(),UserWalletTypeEnum.CURRENCY.getCode());

		//扣减币币账户
		UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId,currencyId, MathUtil.zeroSub(quantity));
		userWalletLogService.addLog(userId,currencyId,MathUtil.zeroSub(quantity), WalletLogTypeEnum.OUTPUT.getCode(),
				transferId,"币币账户转出法币账户",userWalletModelDto);


		//添加 法币账户
		UserOtcWalletModelDto userOtcWalletModelDto = userOtcWalletService.modifyWalletUsing(userId,currencyId,quantity);
		userOtcWalletLogService.addLog(userId,currencyId,quantity,WalletLogTypeEnum.INPUT.getCode(),transferId,
				"币币账户转入法币账户",userOtcWalletModelDto);

	}
	/**
	 * 币币转矿池
	 */
	@Transactional
	public void coinTransferPool(Long userId, Long currencyId, BigDecimal quantity){

		this.checkCoinWallet(userId,currencyId,quantity);

		//添加记录
		Long transferId = this.addTransfer(userId,currencyId,quantity, UserWalletTypeEnum.USING.getCode(),UserWalletTypeEnum.POOL.getCode());

		//扣减币币账户
		UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId,currencyId, MathUtil.zeroSub(quantity));
		userWalletLogService.addLog(userId,currencyId,MathUtil.zeroSub(quantity), WalletLogTypeEnum.OUTPUT.getCode(),
				transferId,"币币账户转出矿池账户",userWalletModelDto);


		//添加 矿池账户
		UserPoolWalletModelDto userPoolWalletModelDto = userPoolWalletService.modifyWalletUsing(userId,currencyId,quantity);
		userPoolWalletLogService.addLog(userId,currencyId,quantity,WalletLogTypeEnum.INPUT.getCode(),transferId,
				"币币账户转入法币账户",userPoolWalletModelDto);
	}

	/**
	 * 法币转币币
	 */
	@Transactional
	public void otcTransferCoin(Long userId, Long currencyId, BigDecimal quantity){
		this.checkOtcWallet(userId,currencyId,quantity);

		//添加记录
		Long transferId = this.addTransfer(userId,currencyId,quantity, UserWalletTypeEnum.CURRENCY.getCode(),UserWalletTypeEnum.USING.getCode());

		//扣减法币账户
		UserOtcWalletModelDto userOtcWalletModelDto = userOtcWalletService.modifyWalletUsing(userId,currencyId, MathUtil.zeroSub(quantity));
		userOtcWalletLogService.addLog(userId,currencyId,MathUtil.zeroSub(quantity), WalletLogTypeEnum.OUTPUT.getCode(),
				transferId,"法币账户转出币币账户",userOtcWalletModelDto);


		//添加 币币账户
		UserWalletModelDto userWalletModelDto = userWalletService.modifyWalletUsing(userId,currencyId,quantity);
		userWalletLogService.addLog(userId,currencyId,quantity,WalletLogTypeEnum.INPUT.getCode(),transferId,
				"法币账户转入币币账户",userWalletModelDto);
	}

	/**
	 * 法币转矿池
	 */
	@Transactional
	public void otcTransferPool(Long userId, Long currencyId, BigDecimal quantity){
		this.checkOtcWallet(userId,currencyId,quantity);

		//添加记录
		Long transferId = this.addTransfer(userId,currencyId,quantity, UserWalletTypeEnum.CURRENCY.getCode(),UserWalletTypeEnum.POOL.getCode());

		//扣减法币账户
		UserOtcWalletModelDto userOtcWalletModelDto = userOtcWalletService.modifyWalletUsing(userId,currencyId, MathUtil.zeroSub(quantity));
		userOtcWalletLogService.addLog(userId,currencyId,MathUtil.zeroSub(quantity), WalletLogTypeEnum.OUTPUT.getCode(),
				transferId,"法币账户转出矿池账户",userOtcWalletModelDto);


		//添加 矿池账户
		UserPoolWalletModelDto userPoolWalletModelDto = userPoolWalletService.modifyWalletUsing(userId,currencyId,quantity);
		userPoolWalletLogService.addLog(userId,currencyId,quantity,WalletLogTypeEnum.INPUT.getCode(),transferId,
				"法币账户转入矿池账户",userPoolWalletModelDto);
	}

	/**
	 * 矿池转币币
	 */
	@Transactional
	public void poolTransferCoin(Long userId, Long currencyId, BigDecimal quantity){
		this.checkPoolWallet(userId,currencyId,quantity);

		//添加记录
		Long transferId = this.addTransfer(userId,currencyId,quantity, UserWalletTypeEnum.POOL.getCode(),UserWalletTypeEnum.USING.getCode());

		//扣减矿池账户
		UserPoolWalletModelDto userPoolWalletModelDto = userPoolWalletService.modifyWalletUsing(userId,currencyId, MathUtil.zeroSub(quantity));
		userPoolWalletLogService.addLog(userId,currencyId,MathUtil.zeroSub(quantity), WalletLogTypeEnum.OUTPUT.getCode(),
				transferId,"矿池账户转出币币账户",userPoolWalletModelDto);


		//添加 资产账户
		UserWalletModelDto userWalletModelDto  = userWalletService.modifyWalletUsing(userId,currencyId,quantity);
		userWalletLogService.addLog(userId,currencyId,quantity,WalletLogTypeEnum.INPUT.getCode(),transferId,
				"矿池账户转入币币账户",userWalletModelDto);
	}

	/**
	 * 矿池转法币
	 */
	@Transactional
	public void poolTransferOtc(Long userId, Long currencyId, BigDecimal quantity){
		this.checkPoolWallet(userId,currencyId,quantity);

		//添加记录
		Long transferId = this.addTransfer(userId,currencyId,quantity, UserWalletTypeEnum.POOL.getCode(),UserWalletTypeEnum.CURRENCY.getCode());

		//扣减矿池账户
		UserPoolWalletModelDto userPoolWalletModelDto = userPoolWalletService.modifyWalletUsing(userId,currencyId, MathUtil.zeroSub(quantity));
		userPoolWalletLogService.addLog(userId,currencyId,MathUtil.zeroSub(quantity), WalletLogTypeEnum.OUTPUT.getCode(),
				transferId,"矿池账户转出法币账户",userPoolWalletModelDto);


		//添加 资产账户
		UserOtcWalletModelDto userWalletModelDto  = userOtcWalletService.modifyWalletUsing(userId,currencyId,quantity);
		userOtcWalletLogService.addLog(userId,currencyId,quantity,WalletLogTypeEnum.INPUT.getCode(),transferId,
				"矿池账户转入法币账户",userWalletModelDto);
	}

	/**
	 * 划转
	 */
	@Transactional
	@Override
	public void transfer(Long userId, Integer sourceId, Integer targetId, Long currencyId, BigDecimal quantity) {
		Integer usingType= UserWalletTypeEnum.USING.getCode();
		Integer currency = UserWalletTypeEnum.CURRENCY.getCode();
		Integer pool = UserWalletTypeEnum.POOL.getCode();

		if (usingType.equals(sourceId)){
			if (currency.equals(targetId)){
				this.coinTransferOtc(userId,currencyId,quantity);
			}else if (pool.equals(targetId)){
				this.coinTransferPool(userId,currencyId,quantity);
			}
		}else if (currency.equals(sourceId)){
			if (usingType.equals(targetId)){
				this.otcTransferCoin(userId,currencyId,quantity);
			}else if (pool.equals(targetId)){
				this.otcTransferPool(userId,currencyId,quantity);
			}
		}else if (pool.equals(sourceId)){
			if (usingType.equals(targetId)){
				this.poolTransferCoin(userId,currencyId,quantity);
			}else if (currency.equals(targetId)){
				this.poolTransferOtc(userId,currencyId,quantity);
			}
		}

	}
}
