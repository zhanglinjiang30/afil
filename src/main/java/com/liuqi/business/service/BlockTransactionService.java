package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.BlockTransactionModel;
import com.liuqi.business.model.BlockTransactionModelDto;

import java.math.BigDecimal;

public interface BlockTransactionService extends BaseService<BlockTransactionModel,BlockTransactionModelDto>{

    void addTransaction(Long height, String fromAddress, String toAddress, Long currencyId, BigDecimal amont, BigDecimal fee, String txHash);

    BlockTransactionModelDto getByTxHash(String txHash);
}
