package com.liuqi.business.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.PublicOfferRecordModel;
import com.liuqi.business.model.PublicOfferRecordModelDto;


import com.liuqi.business.service.PublicOfferRecordService;
import com.liuqi.business.mapper.PublicOfferRecordMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PublicOfferRecordServiceImpl extends BaseServiceImpl<PublicOfferRecordModel,PublicOfferRecordModelDto> implements PublicOfferRecordService{

	@Autowired
	private PublicOfferRecordMapper publicOfferRecordMapper;
	

	@Override
	public BaseMapper<PublicOfferRecordModel,PublicOfferRecordModelDto> getBaseMapper() {
		return this.publicOfferRecordMapper;
	}

	@Override
	public void addRecord(Long userId, Long offerId, BigDecimal quantity,Long currencyId,BigDecimal unitPrice,BigDecimal payPrice) {
		PublicOfferRecordModelDto publicOfferRecordModelDto = new PublicOfferRecordModelDto();
		publicOfferRecordModelDto.setAddressId(userId);
		publicOfferRecordModelDto.setPublicOfferId(offerId);
		publicOfferRecordModelDto.setQuantity(quantity);
		publicOfferRecordModelDto.setCurrencyId(currencyId);
		publicOfferRecordModelDto.setUnitPrice(unitPrice);
		publicOfferRecordModelDto.setRealPrice(payPrice);
		this.insert(publicOfferRecordModelDto);
	}

	public List<Long> getAddresIdsByOfferId(Long offerId){
		return publicOfferRecordMapper.getAddresIdsByOfferId(offerId);
	}

	@Override
	public PageInfo<PublicOfferRecordModelDto> getRecordList(Integer pageNum, Integer pageSize, PublicOfferRecordModelDto publicOfferRecordModelDto) {
		PageHelper.startPage(pageNum,pageSize);

		List<PublicOfferRecordModelDto> list = publicOfferRecordMapper.getRecordList(publicOfferRecordModelDto);

		return new PageInfo<>(list);
	}

	@Override
	public BigDecimal getAlreadyAmountByOfferId(Long userId, Long offerId) {
		return publicOfferRecordMapper.getAlreadyAmountByOfferId(userId,offerId);
	}
}
