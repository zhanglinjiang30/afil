package com.liuqi.business.service.impl;


import com.liuqi.business.enums.ConfirmStatusEnum;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.model.BlockModel;
import com.liuqi.business.service.TableIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.BlockTransactionUnconfirmModel;
import com.liuqi.business.model.BlockTransactionUnconfirmModelDto;


import com.liuqi.business.service.BlockTransactionUnconfirmService;
import com.liuqi.business.mapper.BlockTransactionUnconfirmMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BlockTransactionUnconfirmServiceImpl extends BaseServiceImpl<BlockTransactionUnconfirmModel, BlockTransactionUnconfirmModelDto> implements BlockTransactionUnconfirmService {

    @Autowired
    private BlockTransactionUnconfirmMapper blockTransactionUnconfirmMapper;
    @Autowired
    private TableIdService tableIdService;

    @Override
    public BaseMapper<BlockTransactionUnconfirmModel, BlockTransactionUnconfirmModelDto> getBaseMapper() {
        return this.blockTransactionUnconfirmMapper;
    }

    @Override
    @Transactional
    public void insert(BlockTransactionUnconfirmModel t) {
        t.setId(tableIdService.getNextId(TableIdNameEnum.BLOCK));
        if (t.getCreateTime() == null) {
            t.setCreateTime(new Date());
        }
        if (t.getUpdateTime() == null) {
            t.setUpdateTime(new Date());
        }
        if (t.getVersion() == null) {
            t.setVersion(0);
        }
        this.getBaseMapper().insert(t);
    }

    @Override
    @Transactional
    public void addUnconfirmTranscation(String fromAddress, String toAddress, BigDecimal amouont, Long currencyId, BigDecimal fee, String txHash, Long exId) {
        BlockTransactionUnconfirmModel b = new BlockTransactionUnconfirmModel();
        b.setFromAddress(fromAddress);
        b.setToAddress(toAddress);
        b.setCurrencyId(currencyId);
        b.setFee(fee);
        b.setAmount(amouont);
        b.setConfirm(ConfirmStatusEnum.UNCONFIRM.getCode());
        b.setTxHash(txHash);
        b.setExId(exId);
        insert(b);
    }

    @Override
    public List<BlockTransactionUnconfirmModelDto> getAllUnconfrimTransaction() {
        BlockTransactionUnconfirmModelDto params = new BlockTransactionUnconfirmModelDto();
        params.setConfirm(ConfirmStatusEnum.UNCONFIRM.getCode());
        return queryListByDto(params, false);
    }

    @Override
    @Transactional
    public void modifyToConfirmed(Long id) {
        BlockTransactionUnconfirmModelDto b = getById(id, false);
        if (b.getConfirm().compareTo(ConfirmStatusEnum.UNCONFIRM.getCode()) == 0) {
            b.setConfirm(ConfirmStatusEnum.CONFIRMED.getCode());
            update(b);
        }
    }
}
