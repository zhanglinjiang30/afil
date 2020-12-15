package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.AddressHoldingRecordModel;
import com.liuqi.business.model.AddressHoldingRecordModelDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressHoldingRecordMapper extends BaseMapper<AddressHoldingRecordModel,AddressHoldingRecordModelDto>{

    void deleteByDeviceId(String deviceId);

    List<AddressHoldingRecordModelDto> getByDeviceId(String deviceId);

    List<AddressHoldingRecordModelDto> getAddressByLikeName(String deviceId, String name);

    AddressHoldingRecordModelDto getByDeviceIdAndAddressId(String deviceId, Long addressId);

    void disableDisplay(String deviceId);

    AddressHoldingRecordModelDto getByAddressId(Long addressId);

    void deleteByAddressId(Long addressId);
}
