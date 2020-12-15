package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.PublicOfferRecordModel;
import com.liuqi.business.model.PublicOfferRecordModelDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;


public interface PublicOfferRecordMapper extends BaseMapper<PublicOfferRecordModel,PublicOfferRecordModelDto>{


    List<Long> getAddresIdsByOfferId(Long offerId);

    List<PublicOfferRecordModelDto> getRecordList(PublicOfferRecordModelDto publicOfferRecordModelDto);

    BigDecimal getAlreadyAmountByOfferId(@Param("userId") Long userId, @Param("offerId") Long offerId);
}
