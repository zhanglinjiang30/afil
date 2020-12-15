package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.BlockTransactionUnconfirmModel;
import com.liuqi.business.model.BlockTransactionUnconfirmModelDto;

import java.math.BigDecimal;
import java.util.List;

public interface BlockTransactionUnconfirmService extends BaseService<BlockTransactionUnconfirmModel,BlockTransactionUnconfirmModelDto>{

    /**
     * @Description 添加待确认交易
     * @Date 11:16 2020/8/10
     */
    void addUnconfirmTranscation(String fromAddress, String toAddress, BigDecimal amouont, Long currencyId, BigDecimal fee, String txHash, Long exId);

    List<BlockTransactionUnconfirmModelDto> getAllUnconfrimTransaction();

    /**
     * @Description 待确认交易变更为已确认状态
     * @Date 12:14 2020/8/10
     */
    void modifyToConfirmed(Long id);
}
