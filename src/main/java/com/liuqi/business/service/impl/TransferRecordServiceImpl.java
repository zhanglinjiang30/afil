package com.liuqi.business.service.impl;


import com.github.pagehelper.PageInfo;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.business.websocket.ActivePushHandle;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.mq.BlockProducer;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.BitcoinAddressUtils;
import com.liuqi.utils.MathUtil;
import com.liuqi.utils.ShiroPasswdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.TransferRecordMapper;
import reactor.rx.stream.PeriodicTimerStream;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class TransferRecordServiceImpl extends BaseServiceImpl<TransferRecordModel,TransferRecordModelDto> implements TransferRecordService{

	@Autowired
	private TransferRecordMapper transferRecordMapper;
	@Autowired
	private CurrencyConfigService currencyConfigService;
	@Autowired
	private AddressRecordService addressRecordService;
	@Autowired
	private BlockTransactionUnconfirmService blockTransactionUnconfirmService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ActivePushHandle activePushHandle;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private BlockProducer blockProducer;

	@Autowired
	private UserWalletLogService userWalletLogService;

	@Override
	public BaseMapper<TransferRecordModel,TransferRecordModelDto> getBaseMapper() {
		return this.transferRecordMapper;
	}

	@Override
	protected void doMode(TransferRecordModelDto dto) {
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setFeeCurrencyName(currencyService.getNameById(dto.getFeeCurrencyId()));
	}

	@Override
	@Transactional
	public ReturnResponse doTransfer(LoginDto loginDto, String toAddress, Long currencyId, BigDecimal amount, String tradePwd) {
		AddressHoldingRecordModelDto holdingRecordModelDto = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (holdingRecordModelDto == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		if (!toAddress.startsWith("S") || toAddress.length() < 27 || toAddress.length() > 35) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message58"));
		}
		Long fromAddressId = holdingRecordModelDto.getAddressId();
		AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(fromAddressId);
		if (addressRecordModelDto == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message100"));
		}
		AddressHoldingRecordModelDto mainHoldingAddress = addressHoldingRecordService.getMainAddress(loginDto);
		if (mainHoldingAddress == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message30"));
		}
		if (!StringUtils.equals(mainHoldingAddress.getTradePwd(), ShiroPasswdUtil.getUserPwd(tradePwd))) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("errorMessage5"));
		}
		if (addressRecordModelDto.getActive().compareTo(ActiveStatusEnum.ACTIVE.getCode()) != 0) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message101"));
		}
		AddressRecordModelDto target = addressRecordService.getByAddress(toAddress);
		if (target == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message102"));
		}
		if (fromAddressId.compareTo(target.getId()) == 0) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message103"));
		}
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message104"));
		}
		UserWalletModelDto userWalletModelDto = userWalletService.getByUserAndCurrencyId(fromAddressId, currencyId);
		if (userWalletModelDto.getUsing().compareTo(amount) < 0) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message11"));
		}
		// 抓取矿工费
		Long sucCurrencyId = currencyService.getByName("AFIL").getId();
		BigDecimal fee = getTransferFee(currencyId);
		UserWalletModelDto userWallet = userWalletService.getByUserAndCurrencyId(fromAddressId, currencyId);
		if (userWallet.getUsing().compareTo(fee) < 0) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message105"));
		}
		// 修改资产
		UserWalletModelDto modifyOWallet = userWalletService.modifyWalletUsing(fromAddressId, currencyId, MathUtil.zeroSub(amount));
		UserWalletModelDto modifyFeeWallet = userWalletService.modifyWalletUsing(fromAddressId, currencyId, MathUtil.zeroSub(fee));
		UserWalletModelDto modifyTWallet = userWalletService.modifyWalletUsing(target.getId(), currencyId, amount);

		userWalletLogService.addLog(fromAddressId,currencyId,MathUtil.zeroSub(amount), WalletLogTypeEnum.OUTPUT.getCode(),
				-1L,"转账金额",modifyOWallet);

		userWalletLogService.addLog(fromAddressId,currencyId,MathUtil.zeroSub(fee), WalletLogTypeEnum.OUT_SERVICE_CHARGE.getCode(),
				-1L,"转账手续费",modifyFeeWallet);

		userWalletLogService.addLog(target.getId(),currencyId,amount, WalletLogTypeEnum.INPUT.getCode(),
				-1L,"转入金额",modifyTWallet);

		if (sucCurrencyId.compareTo(currencyId) == 0 &&
				target.getActive().compareTo(ActiveStatusEnum.UNACTIVE.getCode()) == 0) {

			target.setActive(ActiveStatusEnum.ACTIVING.getCode());
			target.setPreActiveAid(fromAddressId);
			addressRecordService.update(target);
			blockProducer.sendActiveMessage(target.getId());
		}
		// 插入转账记录
		TransferRecordModel transferRecordModel = new TransferRecordModel();
		transferRecordModel.setFromAddress(addressRecordModelDto.getAddress());
		transferRecordModel.setToAddress(toAddress);
		transferRecordModel.setStatus(ConfirmStatusEnum.UNCONFIRM.getCode());
		transferRecordModel.setAmount(amount);
		transferRecordModel.setType(TransferTypeEnum.TRNASFER.getCode());
		transferRecordModel.setCurrencyId(currencyId);
		transferRecordModel.setFeeCurrencyId(sucCurrencyId);
		transferRecordModel.setFee(fee);
		String txHash = BitcoinAddressUtils.getHash(String.valueOf(System.currentTimeMillis()) + fromAddressId);
		transferRecordModel.setTxHash(txHash);
		insert(transferRecordModel);
		// 转移到待确认交易记录
		blockTransactionUnconfirmService.addUnconfirmTranscation(addressRecordModelDto.getAddress(), toAddress, amount, currencyId, fee, txHash, transferRecordModel.getId());
		return ReturnResponse.backSuccess();
	}

	private BigDecimal getTransferFee(Long currencyId) {
		CurrencyConfigModelDto currencyConfigModelDto = currencyConfigService.getByCurrencyId(currencyId);
		return currencyConfigModelDto.getMine();
	}

	@Override
	public ReturnResponse getTxList(LoginDto loginDto, Integer pageNum, Integer pageSize) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(a.getAddressId(), false);
		if (addressRecordModelDto == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message106"));
		}
		TransferRecordModelDto transferRecordModel = new TransferRecordModelDto();
		transferRecordModel.setSearchAddress(addressRecordModelDto.getAddress());
		PageInfo<TransferRecordModelDto> pageInfo = queryFrontPageByDto(transferRecordModel, pageNum, pageSize);
		pageInfo.getList().forEach(m -> {
			this.dtoSetParam(m,addressRecordModelDto);
		});
		return ReturnResponse.backSuccess(pageInfo);
	}

	public TransferRecordModelDto getRecordDetail(Long addressId , Long id){
		AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(addressId);
		TransferRecordModelDto transferRecordModelDto = this.getById(id);
		dtoSetParam(transferRecordModelDto,addressRecordModelDto);
		return transferRecordModelDto;
	}

	private void dtoSetParam(TransferRecordModelDto m,AddressRecordModelDto addressRecordModelDto){
		if (m.getType().compareTo(TransferTypeEnum.TRNASFER.getCode()) == 0 &&
				StringUtils.equals(m.getFromAddress(), addressRecordModelDto.getAddress())) {
			m.setAmount(MathUtil.zeroSub(m.getAmount()));
		}
		if (m.getType().compareTo(TransferTypeEnum.TRNASFER.getCode()) == 0) {
			m.setSearchAddress(StringUtils.equals(m.getFromAddress(), addressRecordModelDto.getAddress()) ? m.getToAddress() : m.getFromAddress());
		} else if (m.getType().compareTo(TransferTypeEnum.TRUST_GATEWAY.getCode()) == 0) {
			m.setSearchAddress(m.getToAddress());
		} else if (m.getType().compareTo(TransferTypeEnum.MINING.getCode()) == 0) {
			m.setSearchAddress(m.getFromAddress());
		} else if (m.getType().compareTo(TransferTypeEnum.EXTRACT.getCode()) == 0) {
			m.setSearchAddress(m.getToAddress());
		}
	}

	@Override
	@Transactional
	public void addRecord(Integer type, String fromAddress, String toAddress, BigDecimal amount, Long currencyId, Long feeCurrencyId, BigDecimal fee) {
		TransferRecordModel transferRecordModel = new TransferRecordModel();
		transferRecordModel.setFromAddress(fromAddress);
		transferRecordModel.setToAddress(toAddress);
		transferRecordModel.setStatus(ConfirmStatusEnum.UNCONFIRM.getCode());
		transferRecordModel.setType(type);
		transferRecordModel.setAmount(BigDecimal.ZERO);
		transferRecordModel.setCurrencyId(currencyId);
		transferRecordModel.setFeeCurrencyId(feeCurrencyId);
		transferRecordModel.setFee(fee);
		String txHash = BitcoinAddressUtils.getHash(String.valueOf(System.currentTimeMillis()) + fromAddress + toAddress);
		transferRecordModel.setTxHash(txHash);
		insert(transferRecordModel);

		// 转移到待确认交易记录
		blockTransactionUnconfirmService.addUnconfirmTranscation(fromAddress, toAddress, amount, currencyId, fee, txHash, transferRecordModel.getId());
	}

	@Override
	@Transactional
	public void modifyToConfirm(Long id, Long height) {
		TransferRecordModelDto transferRecordModel = getById(id, false);
		transferRecordModel.setStatus(ConfirmStatusEnum.CONFIRMED.getCode());
		transferRecordModel.setHeight(height);
		update(transferRecordModel);
	}
}
