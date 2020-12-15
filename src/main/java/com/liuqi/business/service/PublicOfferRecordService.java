package com.liuqi.business.service;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.PublicOfferRecordModel;
import com.liuqi.business.model.PublicOfferRecordModelDto;

import java.math.BigDecimal;
import java.util.List;

public interface PublicOfferRecordService extends BaseService<PublicOfferRecordModel,PublicOfferRecordModelDto>{


    void addRecord(Long userId, Long offerId, BigDecimal quantity,Long currencyId,BigDecimal unitPrice,BigDecimal payPrice);

    List<Long> getAddresIdsByOfferId(Long offerId);

    PageInfo<PublicOfferRecordModelDto> getRecordList(Integer pageNum,Integer pageSize,PublicOfferRecordModelDto publicOfferRecordModelDto);

    BigDecimal getAlreadyAmountByOfferId(Long userId,Long offerId);
}
