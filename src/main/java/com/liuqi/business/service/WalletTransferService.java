package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.WalletTransferModel;
import com.liuqi.business.model.WalletTransferModelDto;

import java.math.BigDecimal;

public interface WalletTransferService extends BaseService<WalletTransferModel,WalletTransferModelDto>{

    Long addTransfer(Long userId, Long currencyId, BigDecimal quantity, Integer source, Integer target);

    void coinTransferOtc(Long userId, Long currencyId, BigDecimal quantity);

    void coinTransferPool(Long userId, Long currencyId, BigDecimal quantity);

    void otcTransferCoin(Long userId, Long currencyId, BigDecimal quantity);

    void otcTransferPool(Long userId, Long currencyId, BigDecimal quantity);

    void poolTransferCoin(Long userId, Long currencyId, BigDecimal quantity);

    void poolTransferOtc(Long userId, Long currencyId, BigDecimal quantity);


    void transfer(Long userId,Integer sourceId,Integer targetId,Long currencyId, BigDecimal quantity);

}
