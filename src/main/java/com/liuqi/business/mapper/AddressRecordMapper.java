package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.AddressRecordModel;
import com.liuqi.business.model.AddressRecordModelDto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AddressRecordMapper extends BaseMapper<AddressRecordModel,AddressRecordModelDto>{

    AddressRecordModelDto getByPassphraseIdAndIndex(Long passphraseId, Integer index);

    AddressRecordModelDto getByAddress(String address);

    AddressRecordModelDto getByPrivateKey(String privateKey);

    int updateCount(Long id, Integer activeDeviceCount, Integer activeAddressCount);

    void updateAirDropAmount(List<Long> list, BigDecimal amount);

    List<Long> getAll();
}
