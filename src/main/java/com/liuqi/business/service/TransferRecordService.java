package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.TransferRecordModel;
import com.liuqi.business.model.TransferRecordModelDto;
import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;

public interface TransferRecordService extends BaseService<TransferRecordModel,TransferRecordModelDto>{

    /**
     * @Description 转账
     * @Date 10:37 2020/8/12
     */
    ReturnResponse doTransfer(LoginDto loginDto, String toAddress, Long currencyId, BigDecimal amount, String tradePwd);

    /**
     * @Description 转账记录
     * @Date 15:50 2020/8/12
     */
    ReturnResponse getTxList(LoginDto loginDto, Integer pageNum, Integer pageSize);

    /**
     * @Description 转账记录
     * @Date 15:50 2020/8/12
     */
    void addRecord(Integer type, String fromAddress, String toAddress, BigDecimal amount, Long currencyId, Long feeCurrencyId, BigDecimal fee);

    /**
     * @Description 待确认状态修改为已确认
     * @Date 15:50 2020/8/12
     */
    void modifyToConfirm(Long id, Long height);

    TransferRecordModelDto getRecordDetail(Long addressId,Long id);

}
