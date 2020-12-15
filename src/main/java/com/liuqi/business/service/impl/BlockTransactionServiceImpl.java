package com.liuqi.business.service.impl;

import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.BlockTransactionModel;
import com.liuqi.business.model.BlockTransactionModelDto;


import com.liuqi.business.service.BlockTransactionService;
import com.liuqi.business.mapper.BlockTransactionMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class BlockTransactionServiceImpl extends BaseServiceImpl<BlockTransactionModel,BlockTransactionModelDto> implements BlockTransactionService{

	@Autowired
	private BlockTransactionMapper blockTransactionMapper;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public BaseMapper<BlockTransactionModel,BlockTransactionModelDto> getBaseMapper() {
		return this.blockTransactionMapper;
	}

	@Override
	protected void doMode(BlockTransactionModelDto dto) {
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	@Transactional
	public void addTransaction(Long height, String fromAddress, String toAddress, Long currencyId, BigDecimal amont, BigDecimal fee, String txHash) {
		BlockTransactionModel blockTransactionModel = new BlockTransactionModel();
		blockTransactionModel.setHeight(height);
		blockTransactionModel.setFromAddress(fromAddress);
		blockTransactionModel.setToAddress(toAddress);
		blockTransactionModel.setCurrencyId(currencyId);
		blockTransactionModel.setAmount(amont);
		blockTransactionModel.setFee(fee);
		blockTransactionModel.setTxHash(txHash);
		insert(blockTransactionModel);
	}

	@Override
	public BlockTransactionModelDto getByTxHash(String txHash) {
		return blockTransactionMapper.getByTxHash(txHash);
	}
}
