package com.liuqi.business.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.utils.DateUtil;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.CrowdfundRecordMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CrowdfundRecordServiceImpl extends BaseServiceImpl<CrowdfundRecordModel, CrowdfundRecordModelDto> implements CrowdfundRecordService {

	@Autowired
	private CrowdfundRecordMapper crowdfundRecordMapper;

	@Autowired
	private UserLevelService userLevelService;

	@Autowired
	private AddressHoldingRecordService addressHoldingRecordService;

	@Autowired
	private AddressRecordService addressRecordService;

	@Autowired
	private UserWalletService userWalletService;

	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<CrowdfundRecordModel, CrowdfundRecordModelDto> getBaseMapper() {
		return this.crowdfundRecordMapper;
	}

	@Override
	public List<CrowdfundRecordModelDto> getByAddressIdAndInfoId(Long addressId, Long fundInfoId) {
		return crowdfundRecordMapper.getByAddressIdAndInfoId(addressId,fundInfoId);
	}

	public BigDecimal getTotalByAddressId(Long addressId,Long fundInfoId){
		List<CrowdfundRecordModelDto> list = this.getByAddressIdAndInfoId(addressId,fundInfoId);
		BigDecimal sum = BigDecimal.ZERO;
		for (CrowdfundRecordModelDto recordDto : list){
			sum = MathUtil.add(sum,recordDto.getQuantity());
		}
		return sum;
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void addRecord(Long addressId, Long fundInfoId, BigDecimal amount, Long currencyId,
						  BigDecimal gainAmount,Long gainCurrencyId,BigDecimal unitPrice) {
		CrowdfundRecordModel crowdfundRecordModel = new CrowdfundRecordModel();
		crowdfundRecordModel.setAddressId(addressId);
		crowdfundRecordModel.setCrowdfundInfoId(fundInfoId);
		crowdfundRecordModel.setCurrencyId(currencyId);
		crowdfundRecordModel.setQuantity(amount);
		crowdfundRecordModel.setGainQuantity(gainAmount);
		crowdfundRecordModel.setGainCurrencyId(gainCurrencyId);
		crowdfundRecordModel.setUnitPrice(unitPrice);

		this.insert(crowdfundRecordModel);
	}

	@Override
	public List<Map<String,Object>> getSummaryByInfoId(Long infoId) {
		return crowdfundRecordMapper.getSummaryByInfoId(infoId);
	}

	@Override
	public PageInfo<CrowdfundRecordModelDto> getRecordList(Integer pageNum, Integer pageSize,
														   CrowdfundRecordModelDto crowdfundRecordModelDto) {
		PageHelper.startPage(pageNum,pageSize);
		List<CrowdfundRecordModelDto> list = crowdfundRecordMapper.getRecordList(crowdfundRecordModelDto);
		return new PageInfo<>(list);
	}

	public Map<String,Object> getTeamCount(Long addressId){
		Map<String,Object> result = new HashMap<>();
		List<Long> directIds = userLevelService.getAssignSubIdList(addressId,1);
		List<Long> allSub = userLevelService.getAllSubIdList(addressId);

		Integer validCount = 0;
		if (directIds.size()>0){
			validCount = this.getValidCountByUserIds(directIds).size();
		}

		result.put("directCount",directIds.size());
		result.put("teamCount",allSub.size());
		result.put("validCount",validCount);

		return result;
	}

	public PageInfo<Map<String,Object>> getTeamList(Long addressId,Long currencyId,Integer pageNum,Integer pageSize){
		List<Long> directIds = userLevelService.getAssignSubIdList(addressId,1);
		List<Map<String,Object>> result = new ArrayList<>();
		PageHelper.startPage(pageNum,pageSize);

		String currencyName = currencyService.getNameById(currencyId);

		directIds.forEach(id->{
			Map<String,Object> item = new HashMap<>();
			BigDecimal totalVote = this.getQuantityByAddressId(id,currencyId);
			if (totalVote == null){
				return;
			}
			UserLevelModelDto userLevelModelDto = userLevelService.getByUserId(id);
			AddressRecordModelDto addressRecordModelDto = addressRecordService.getById(id);
//			item.put("name",addressHoldingRecordService.getByAddressId(id).getName());
			item.put("address",addressRecordModelDto.getAddress());
			List<Long> subDirectIds = userLevelService.getAssignSubIdList(id,1);
			List<Long> subAllIds = userLevelService.getAllSubIdList(id);
			item.put("directCount",subDirectIds.size());
			item.put("teamCount",subAllIds.size());
			BigDecimal teamCoinHold = userWalletService.getTeamCoinHold(userLevelModelDto.getTreeInfo(), userLevelModelDto.getTreeLevel(), currencyId);
			item.put("teamCoinHold",teamCoinHold== null?BigDecimal.ZERO:teamCoinHold);
			item.put("currencyName",currencyName);
			item.put("createTime", DateUtil.formatDateStr(addressRecordModelDto.getCreateTime()));

			result.add(item);
		});
		return new PageInfo<>(result);
	}

	private BigDecimal getQuantityByAddressId(Long addressId,Long targetCurrencyId){
		return crowdfundRecordMapper.getQuantityByAddressId(addressId,targetCurrencyId);
	}

	public List<Integer> getValidCountByUserIds(List<Long> userIds){
		return crowdfundRecordMapper.getValidCountByUserIds(userIds);
	}
}
