package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.AddressRecordModel;
import com.liuqi.business.model.AddressRecordModelDto;
import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;
import java.util.List;

public interface AddressRecordService extends BaseService<AddressRecordModel,AddressRecordModelDto>{

    AddressRecordModelDto getByPassphraseIdAndIndex(Long passphraseId, Integer index);

    AddressRecordModelDto getByAddress(String address);

    Long getIdByAddress(String address);

    AddressRecordModelDto getByPrivateKey(String privateKey);

    ReturnResponse doActive(LoginDto loginDto, String tradePwd);

    void updateCount(Long id, Integer activeDeviceCount, Integer activeAddressCount);

    void updateAirDropAmount(List<Long> list, BigDecimal amount);

    List<Long> getAll();

    BigDecimal getTrustFee();

    void activeAddress(LoginDto loginDto,String toAddress,String tradePwd);
}
