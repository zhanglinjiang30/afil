package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.enums.ActiveStatusEnum;
import com.liuqi.business.enums.TransferTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MathUtil;
import com.liuqi.utils.ShiroPasswdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.AddressRecordMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AddressRecordServiceImpl extends BaseServiceImpl<AddressRecordModel,AddressRecordModelDto> implements AddressRecordService{

	@Autowired
	private AddressRecordMapper addressRecordMapper;
	@Autowired
	private PassphraseDeviceService passphraseDeviceService;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private ActiveRecordService activeRecordService;
	@Autowired
	private TransferRecordService transferRecordService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private RedisRepository redisRepository;

	@Override
	protected void doMode(AddressRecordModelDto dto) {
		dto.setPassphrase(passphraseDeviceService.getById(dto.getPassphraseId()).getPassphrase());
		AddressHoldingRecordModel addressHoldingRecordModel = addressHoldingRecordService.getByAddressId(dto.getId());
		String tradePwd = addressHoldingRecordModel != null ? addressHoldingRecordModel.getTradePwd(): "";
		dto.setTradePwd(tradePwd);
	}

	@Override
	public BaseMapper<AddressRecordModel,AddressRecordModelDto> getBaseMapper() {
		return this.addressRecordMapper;
	}

	@Override
	public AddressRecordModelDto getByPassphraseIdAndIndex(Long passphraseId, Integer index) {
		return addressRecordMapper.getByPassphraseIdAndIndex(passphraseId, index);
	}

	@Override
	public AddressRecordModelDto getByAddress(String address) {
		String key = KeyConstant.KEY_ADDRESS_RECORD_ADDRESS + address;
		String value = redisRepository.getString(key);
		if (StringUtils.isNotEmpty(value)) {
			return JSONObject.parseObject(value, AddressRecordModelDto.class);
		}
		AddressRecordModelDto a = addressRecordMapper.getByAddress(address);
		if (a != null) {
			redisRepository.set(key, JSONObject.toJSONString(a));
		}
		return a;
	}

	@Override
	public Long getIdByAddress(String address) {
		AddressRecordModelDto a = getByAddress(address);
		return a == null ? 0L : a.getId();
	}

	@Override
	public AddressRecordModelDto getById(Long id) {
		String key = KeyConstant.KEY_ADDRESS_RECORD_ID + id;
		String value = redisRepository.getString(key);
		if (StringUtils.isNotEmpty(value)) {
			return JSONObject.parseObject(value, AddressRecordModelDto.class);
		}
		AddressRecordModelDto a = addressRecordMapper.getById(id);
		if (a != null) {
			doMode(a);
			redisRepository.set(key, JSONObject.toJSONString(a));
		}
		return a;
	}

	@Override
	public AddressRecordModelDto getByPrivateKey(String privateKey) {
		return addressRecordMapper.getByPrivateKey(privateKey);
	}

	@Override
	@Transactional
	public void updateCount(Long id, Integer activeDeviceCount, Integer activeAddressCount) {
		addressRecordMapper.updateCount(id, activeDeviceCount, activeAddressCount);
		redisRepository.del(KeyConstant.KEY_ADDRESS_RECORD_ID + id);
		redisRepository.delete(KeyConstant.KEY_ADDRESS_RECORD_ADDRESS + "*");
	}

	@Override
	@Transactional
	public void updateAirDropAmount(List<Long> list, BigDecimal amount) {
		addressRecordMapper.updateAirDropAmount(list, amount);
		redisRepository.delete(KeyConstant.KEY_ADDRESS_RECORD_ID + "*");
		redisRepository.delete(KeyConstant.KEY_ADDRESS_RECORD_ADDRESS + "*");
	}

	@Override
	@Transactional
	public ReturnResponse doActive(LoginDto loginDto, String tradePwd) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		AddressHoldingRecordModelDto mainHoldingAddress = addressHoldingRecordService.getMainAddress(loginDto);
		if (mainHoldingAddress == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message30"));
		}
		if (!StringUtils.equals(mainHoldingAddress.getTradePwd(), ShiroPasswdUtil.getUserPwd(tradePwd))) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("errorMessage5"));
		}
		// 地址变更为已激活
		AddressRecordModelDto aa = getById(a.getAddressId(), false);
		if (aa.getActive().compareTo(ActiveStatusEnum.ACTIVE.getCode()) == 0) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message50"));
		}
		aa.setActive(ActiveStatusEnum.ACTIVE.getCode());
		update(aa);
		// 添加激活记录
		activeRecordService.addActiveRecord(aa.getPreActiveAid(), aa.getId(), aa.getDeviceId());
		Long currencyId = currencyService.getPTId();
		userWalletService.modifyWalletUsing(aa.getId(), currencyId, MathUtil.zeroSub(getTrustFee()));
		String toAddress = configService.queryValueByName(ConfigConstant.CONFIG_GATEWAY_ADDRESS);
		// 添加交易记录
		transferRecordService.addRecord(TransferTypeEnum.TRUST_GATEWAY.getCode(), aa.getAddress(), toAddress,
				BigDecimal.ZERO, currencyId, currencyId, getTrustFee());
		return ReturnResponse.backSuccess();
	}

	@Override
	public void afterUpdateOperate(AddressRecordModel addressRecordModel) {
		redisRepository.del(KeyConstant.KEY_ADDRESS_RECORD_ID + addressRecordModel.getId());
		redisRepository.del(KeyConstant.KEY_ADDRESS_RECORD_ADDRESS + addressRecordModel.getAddress());
	}

	public BigDecimal getTrustFee() {
		String value = configService.queryValueByName(ConfigConstant.CONFIG_ACTIVE_GATEWAY_FEE);
		return StringUtils.isEmpty(value) ? BigDecimal.valueOf(0.00012) : new BigDecimal(value);
	}

	@Override
	public List<Long> getAll() {
		return addressRecordMapper.getAll();
	}

	@Transactional(rollbackFor = Throwable.class)
	public void activeAddress(LoginDto loginDto,String toAddress,String tradePwd){

		AddressHoldingRecordModelDto fromAddressHoldingDto = addressHoldingRecordService.getDisplayAddress(loginDto);

		if (fromAddressHoldingDto == null) {
			throw new BusinessException(MessageSourceHolder.getMessage("message30"));
		}

		Long addressId = fromAddressHoldingDto.getAddressId();


		if (!StringUtils.equals(fromAddressHoldingDto.getTradePwd(), ShiroPasswdUtil.getUserPwd(tradePwd))) {
			throw new BusinessException(MessageSourceHolder.getMessage("errorMessage5"));
		}

		AddressRecordModelDto fromAddressDto = this.getById(addressId);

		if (!ActiveStatusEnum.ACTIVE.getCode().equals(fromAddressDto.getActive())){
			throw new BusinessException(MessageSourceHolder.getMessage("message51"));
		}

		AddressRecordModelDto toAddressDto = this.getByAddress(toAddress);
		if (toAddressDto == null){
			throw new BusinessException(MessageSourceHolder.getMessage("message52"));
		}
		if (toAddressDto.getActive().compareTo(ActiveStatusEnum.ACTIVE.getCode()) == 0) {
			throw new BusinessException(MessageSourceHolder.getMessage("message50"));
		}
		toAddressDto.setActive(ActiveStatusEnum.ACTIVE.getCode());
		toAddressDto.setPreActiveAid(addressId);
		this.update(toAddressDto);
		// 添加激活记录
		activeRecordService.addActiveRecord(toAddressDto.getPreActiveAid(), toAddressDto.getId(), toAddressDto.getDeviceId());
		Long currencyId = currencyService.getPTId();
		//激活消耗
		userWalletService.modifyWalletUsing(addressId, currencyId, MathUtil.zeroSub(getTrustFee()));
		String toGatewayAddress = configService.queryValueByName(ConfigConstant.CONFIG_GATEWAY_ADDRESS);

		// 添加交易记录
		transferRecordService.addRecord(TransferTypeEnum.TRUST_GATEWAY.getCode(), fromAddressDto.getAddress(), toGatewayAddress,
				BigDecimal.ZERO, currencyId, currencyId, getTrustFee());
	}
}
