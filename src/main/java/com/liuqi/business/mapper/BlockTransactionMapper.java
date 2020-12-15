package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.BlockTransactionModel;
import com.liuqi.business.model.BlockTransactionModelDto;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockTransactionMapper extends BaseMapper<BlockTransactionModel,BlockTransactionModelDto>{

    BlockTransactionModelDto getByTxHash(String txHash);

}
