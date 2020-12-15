package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.dto.SDto;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.ActiveRecordMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ActiveRecordServiceImpl extends BaseServiceImpl<ActiveRecordModel,ActiveRecordModelDto> implements ActiveRecordService{

	@Autowired
	private ActiveRecordMapper activeRecordMapper;
	@Autowired
	private UserLevelService userLevelService;
	@Autowired
	private AddressRecordService addressRecordService;
	@Autowired
	private PassphraseDeviceService passphraseDeviceService;
	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private UserAuthService userAuthService;

	@Override
	public BaseMapper<ActiveRecordModel,ActiveRecordModelDto> getBaseMapper() {
		return this.activeRecordMapper;
	}

	@Override
	protected void doMode(ActiveRecordModelDto dto) {
		AddressRecordModelDto a = addressRecordService.getById(dto.getFromAddressId(), false);
		dto.setFromDeviceId(a.getDeviceId());
		dto.setFromAddress(a.getAddress());
		dto.setToAddress(addressRecordService.getById(dto.getToAddressId(), false).getAddress());
	}

	@Override
	@Transactional
	public void addActiveRecord(Long fromAddressId, Long toAddressId, String activeDevice) {
		Long fromPId = addressRecordService.getById(fromAddressId, false).getPassphraseId();
//		PassphraseDeviceModelDto p = passphraseDeviceService.getById(fromPId, false);
//		Integer activeCount = activeRecordMapper.getPActiveDeviceCount(fromPId, activeDevice);
//		log.info("添加激活记录，激活方地址id={}，被激活方地址id={}，被激活方设备id={}", fromAddressId, toAddressId, activeDevice);
//		// 此助记词未激活过此设备
//		if (activeCount == null || activeCount.compareTo(0) <= 0) {
//			log.info("未激活此设备");
//			p.setActiveDevice(p.getActiveDevice() + 1);
//		}
//		// 激活地址数量和激活设备+1
//		p.setActiveCount(p.getActiveCount() + 1);
//		log.info("添加激活记录，助记词激活信息={}", JSONObject.toJSONString(p));
//		passphraseDeviceService.update(p);

		// 地址激活设备次数统计
		AddressRecordModelDto a = addressRecordService.getById(fromAddressId, false);
		Integer addressActiveCount = activeRecordMapper.getAActiveDeviceCount(fromAddressId, activeDevice);
		if (addressActiveCount == null || addressActiveCount.compareTo(0) <= 0) {
			a.setActiveDevice(a.getActiveDevice() + 1);
		}
		a.setActiveCount(a.getActiveCount() + 1);
		addressRecordService.update(a);

		// 插入激活记录
		ActiveRecordModel activeRecordModel = new ActiveRecordModel();
		activeRecordModel.setFromPId(fromPId);
		activeRecordModel.setFromAddressId(fromAddressId);
		activeRecordModel.setToAddressId(toAddressId);
		activeRecordModel.setActiveDevice(activeDevice);
		insert(activeRecordModel);
		//初始化用户层级信息
		userLevelService.initLevel(toAddressId, fromAddressId);

		// 抓取激活方的所有上级- 筛选出助记词
		List<String> parentAddressIdList = userLevelService.getParent(fromAddressId);
//		Map<Long, String> map = new HashMap<>();
		for (String parent : parentAddressIdList) {
//			Long fromPPId = addressRecordService.getById(Long.parseLong(parent), false).getPassphraseId();
			AddressRecordModelDto aa = addressRecordService.getById(Long.parseLong(parent), false);
			Integer parentAddressActiveCount = activeRecordMapper.getAActiveDeviceCount(Long.parseLong(parent), activeDevice);
			if (parentAddressActiveCount == null || parentAddressActiveCount.compareTo(0) <= 0) {
				aa.setActiveDevice(aa.getActiveDevice() + 1);
			}
			aa.setActiveCount(aa.getActiveCount() + 1);
			addressRecordService.update(aa);
//			if (fromPPId.compareTo(fromPId) == 0) {
//				log.info("查找上级助记词，处理过了，跳过");
//				continue;
//			}
//			PassphraseDeviceModelDto pp = passphraseDeviceService.getById(fromPPId, false);
//			if (!map.containsKey(fromPPId)) {
//				map.put(fromPPId, "");
//				Integer parentActiveCount = activeRecordMapper.getPActiveDeviceCount(fromPPId, activeDevice);
//				// 此助记词未激活过此设备
//				if (parentActiveCount == null || parentActiveCount.compareTo(0) <= 0) {
//					pp.setActiveDevice(pp.getActiveDevice() + 1);
//				}
//			}
//			pp.setActiveCount(pp.getActiveCount() + 1);
//			log.info("上级助记词添加激活记录，上级更新后信息={}", JSONObject.toJSONString(pp));
//			passphraseDeviceService.update(pp);
		}
	}

	@Override
	public ReturnResponse getActiveList(LoginDto loginDto, Long currencyId, Integer pageNum, Integer pageSize) {
		AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
		}
		String currencyName = currencyService.getNameById(currencyId);
		ActiveRecordModelDto params = new ActiveRecordModelDto();
		params.setFromAddressId(a.getAddressId());
		PageInfo<ActiveRecordModelDto> pageInfo = queryFrontPageByDto(params, pageNum, pageSize);
		pageInfo.getList().forEach(m -> {
			UserLevelModelDto userLevelModelDto = userLevelService.getByUserId(m.getToAddressId());
			BigDecimal teamCoinHold = userWalletService.getTeamCoinHold(userLevelModelDto.getTreeInfo(), userLevelModelDto.getTreeLevel(), currencyId);
			m.setBalance(teamCoinHold == null ? BigDecimal.ZERO : teamCoinHold);
			m.setCurrencyName(currencyName);
			AddressRecordModelDto aa = addressRecordService.getById(m.getToAddressId(), false);
			m.setActiveCount(aa.getActiveCount());
			m.setActiveDeviceCount(aa.getActiveDevice());
		});
		return ReturnResponse.backSuccess(pageInfo);
	}

	@Override
	@Transactional
	public ReturnResponse modifyName(Long id, String name) {
		ActiveRecordModelDto a = getById(id, false);
		if (a == null) {
			return ReturnResponse.backFail(MessageSourceHolder.getMessage("message49"));
		}
		a.setName(name);
		update(a);
		return ReturnResponse.backSuccess(name);
	}

	@Override
	@Transactional
	public ReturnResponse refreshActiveData() {
		List<UserLevelModelDto> ulist = userLevelService.queryListByDto(null, false);
		ulist.forEach(m -> {
			List<Long> l = userLevelService.getAllSubIdList(m.getUserId());
			SDto sDto = activeRecordMapper.getActiveData(l);
			if (sDto.getActiveAddressCount() == null) {
				sDto.setActiveAddressCount(0);
			}
			if (sDto.getActiveDeviceCount() == null) {
				sDto.setActiveDeviceCount(0);
			}
			log.info("-------------{},{},{}", m.getUserId(), sDto.getActiveDeviceCount(), sDto.getActiveAddressCount());
			addressRecordService.updateCount(m.getUserId(), sDto.getActiveDeviceCount(), sDto.getActiveAddressCount());
		});
		return ReturnResponse.backSuccess();
	}

	public Long getParentByToAddressId(Long toAddressId){
		return activeRecordMapper.getParentByToAddressId(toAddressId);
	}

	@Override
	public List<Long> getToIdsByFromAddressId(Long fromAddressId) {
		return activeRecordMapper.getToIdsByFromAddressId(fromAddressId);
	}
}
