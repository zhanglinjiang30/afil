package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.enums.ActiveStatusEnum;
import com.liuqi.business.enums.WebSocketTypeEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.AddressRecordModelDto;
import com.liuqi.business.model.UserWalletModelDto;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserWalletService;
import com.liuqi.business.websocket.ActivePushHandle;
import com.liuqi.exception.BusinessException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.ShiroPasswdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.AddressHoldingRecordModel;
import com.liuqi.business.model.AddressHoldingRecordModelDto;


import com.liuqi.business.service.AddressHoldingRecordService;
import com.liuqi.business.mapper.AddressHoldingRecordMapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AddressHoldingRecordServiceImpl extends BaseServiceImpl<AddressHoldingRecordModel,AddressHoldingRecordModelDto> implements AddressHoldingRecordService{

	@Autowired
	private AddressHoldingRecordMapper addressHoldingRecordMapper;
	@Autowired
	private AddressRecordService addressRecordService;
	@Autowired
	private ActivePushHandle activePushHandle;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public BaseMapper<AddressHoldingRecordModel,AddressHoldingRecordModelDto> getBaseMapper() {
		return this.addressHoldingRecordMapper;
	}

	@Override
	protected void doMode(AddressHoldingRecordModelDto dto) {
		AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(dto.getAddressId(), false);
		dto.setPrivateKey(addressRecordModelDto.getPrivateKey());
		dto.setAddress(addressRecordModelDto.getAddress());
		// 地址对应的平台币资产
		Long ptId = currencyService.getPTId();
		UserWalletModelDto userWalletModelDto = userWalletService.getByUserAndCurrencyId(dto.getAddressId(), ptId);
		dto.setSucAmount(userWalletModelDto.getUsing());
	}

	@Override
	@Transactional
	public void deleteByDeviceId(String deviceId) {
		addressHoldingRecordMapper.deleteByDeviceId(deviceId);
		redisRepository.del(KeyConstant.KEY_MAIN_ADDRESS + deviceId);
		redisRepository.del(KeyConstant.KEY_DISPLAY_ADDRESS + deviceId);
	}

	@Override
	public List<AddressHoldingRecordModelDto> getAddressesByDeviceId(LoginDto loginDto) {
		AddressHoldingRecordModelDto params = new AddressHoldingRecordModelDto();
		params.setDeviceId(loginDto.getDeviceId());
		params.setSortName("main");
		params.setSortType(" desc");
		List<AddressHoldingRecordModelDto> list = queryListByDto(params, true);
		List<AddressHoldingRecordModelDto> subList = list.subList(1, list.size());
		Collections.sort(subList, new Comparator<AddressHoldingRecordModelDto>() {
			@Override
			public int compare(AddressHoldingRecordModelDto o1, AddressHoldingRecordModelDto o2) {
				return addressRecordService.getById(o1.getAddressId()).getCreateTime().compareTo(addressRecordService.getById(o2.getAddressId()).getCreateTime());
			}
		});
		subList.add(0, list.get(0));
		return subList;
	}

	@Override
	public List<AddressHoldingRecordModelDto> getAddressByLikeName(LoginDto loginDto, String name) {
		List<AddressHoldingRecordModelDto> list = addressHoldingRecordMapper.getAddressByLikeName(loginDto.getDeviceId(), name);
		list.forEach(m -> {
			m.setAddress(addressRecordService.getById(m.getAddressId()).getAddress());
		});
		return list;
	}

	@Override
	public AddressHoldingRecordModelDto getByDeviceIdAndAddressId(String deviceId, Long addressId) {
		return addressHoldingRecordMapper.getByDeviceIdAndAddressId(deviceId, addressId);
	}

	public void deleteByAddressId(Long addressId){
		addressHoldingRecordMapper.deleteByAddressId(addressId);
	}

	@Override
	@Transactional
	public ReturnResponse setDisplay(LoginDto loginDto, Long id) {
		// 先关掉其他显示钱包
		addressHoldingRecordMapper.disableDisplay(loginDto.getDeviceId());
		String key = KeyConstant.KEY_DISPLAY_ADDRESS + loginDto.getDeviceId();
		redisRepository.del(key);

		AddressHoldingRecordModelDto a = getById(id, false);
		a.setDisplay(YesNoEnum.YES.getCode());
		update(a);

		// 获取当前登录者的显示的地址信息
		AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(a.getAddressId());
		if (addressRecordModelDto.getActive().compareTo(ActiveStatusEnum.ACTIVING.getCode()) == 0) {
			activePushHandle.optionsPush(addressRecordModelDto.getId());
		}

		return ReturnResponse.backSuccess();
	}

	@Override
	@Transactional
	public ReturnResponse modifyAddressName(Long id, String name) {
		AddressHoldingRecordModelDto a = getById(id, false);
		a.setName(name);
		update(a);
		return ReturnResponse.backSuccess();
	}

	@Override
	@Transactional
	public void disableDisplay(String deviceId) {
		addressHoldingRecordMapper.disableDisplay(deviceId);
		redisRepository.del(KeyConstant.KEY_DISPLAY_ADDRESS + deviceId);
		redisRepository.del(KeyConstant.KEY_MAIN_ADDRESS + deviceId);
	}

	@Override
	public AddressHoldingRecordModelDto getDisplayAddress(LoginDto loginDto) {
		if (StringUtils.isEmpty(loginDto.getDeviceId())) {
			return null;
		}
		String key = KeyConstant.KEY_DISPLAY_ADDRESS + loginDto.getDeviceId();
		String value = redisRepository.getString(key);
		if (StringUtils.isNotEmpty(value)) {
			return JSONObject.parseObject(value, AddressHoldingRecordModelDto.class);
		}
		AddressHoldingRecordModelDto params = new AddressHoldingRecordModelDto();
		params.setDeviceId(loginDto.getDeviceId());
		params.setDisplay(YesNoEnum.YES.getCode());
		List<AddressHoldingRecordModelDto> list = queryListByDto(params, true);
		if (list != null && !list.isEmpty()) {
			redisRepository.set(key, JSONObject.toJSONString(list.get(0)));
		}
		return list == null || list.isEmpty() ? null : list.get(0);
	}

	@Override
	public AddressHoldingRecordModelDto getMainAddress(LoginDto loginDto) {
		if (StringUtils.isEmpty(loginDto.getDeviceId())) {
			return null;
		}
		String key = KeyConstant.KEY_MAIN_ADDRESS + loginDto.getDeviceId();
		String value = redisRepository.getString(key);
		if (StringUtils.isNotEmpty(value)) {
			return JSONObject.parseObject(value, AddressHoldingRecordModelDto.class);
		}
		AddressHoldingRecordModelDto params = new AddressHoldingRecordModelDto();
		params.setDeviceId(loginDto.getDeviceId());
		params.setMain(YesNoEnum.YES.getCode());
		List<AddressHoldingRecordModelDto> list = queryListByDto(params, true);
		if (list != null && !list.isEmpty()) {
			redisRepository.set(key, JSONObject.toJSONString(list.get(0)));
		}
		return list == null || list.isEmpty() ? null : list.get(0);
	}

	@Override
	public AddressHoldingRecordModelDto getByAddressId(Long addressId) {
		return addressHoldingRecordMapper.getByAddressId(addressId);
	}

	@Override
	public String getNameByAddressId(Long addressId) {
		AddressHoldingRecordModelDto addressHoldingRecordModelDto = this.getByAddressId(addressId);
		return addressHoldingRecordModelDto != null ? addressHoldingRecordModelDto.getName():"";
	}

	/**
	 * 修改交易密码
	 * @param tradePassword
	 * @param newTradePassword
	 * @param userId
	 * @param b
	 */
	@Transactional
	@Override
	public void changeTradePwd(String tradePassword, String newTradePassword, Long userId, boolean b) {
		AddressHoldingRecordModelDto addressHoldingRecordModelDto = this.getByAddressId(userId);
		if (!addressHoldingRecordModelDto.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePassword))){
			throw new BusinessException(MessageSourceHolder.getMessage("h_Message6"));
		}

		addressHoldingRecordModelDto.setTradePwd(ShiroPasswdUtil.getUserPwd(newTradePassword));
		this.update(addressHoldingRecordModelDto);
	}
}
