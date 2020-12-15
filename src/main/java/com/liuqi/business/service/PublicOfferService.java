package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.PublicOfferModel;
import com.liuqi.business.model.PublicOfferModelDto;

import java.math.BigDecimal;

public interface PublicOfferService extends BaseService<PublicOfferModel,PublicOfferModelDto>{


    void buy(Long userId , Long offerId, BigDecimal quantity);
}
