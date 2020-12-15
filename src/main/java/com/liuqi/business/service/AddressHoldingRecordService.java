package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.AddressHoldingRecordModel;
import com.liuqi.business.model.AddressHoldingRecordModelDto;
import com.liuqi.response.ReturnResponse;

import java.util.List;

public interface AddressHoldingRecordService extends BaseService<AddressHoldingRecordModel,AddressHoldingRecordModelDto>{

    /**
     * @Description 删除设备下的所有持有记录
     * @Date 23:40 2020/8/9
     */
    void deleteByDeviceId(String deviceId);
    /**
     *
     */
    void deleteByAddressId(Long addressId);

    /**
     * @Description 获取登录用户的所有地址
     * @Date 23:47 2020/8/9
     */
    List<AddressHoldingRecordModelDto> getAddressesByDeviceId(LoginDto loginDto);

    /**
     * @Description 获取登录用户的所有地址
     * @Date 23:47 2020/8/9
     */
    List<AddressHoldingRecordModelDto> getAddressByLikeName(LoginDto loginDto, String name);

    /**
     * @Description 某设备持有某地址的记录
     * @Date 9:54 2020/8/24
     */
    AddressHoldingRecordModelDto getByDeviceIdAndAddressId(String deviceId, Long addressId);

    /**
     * @Description 设备某设备显示某地址
     * @Date 9:54 2020/8/24
     */
    ReturnResponse setDisplay(LoginDto loginDto, Long id);

    /**
     * @Description 设置某地址名字
     * @Date 9:54 2020/8/24
     */
    ReturnResponse modifyAddressName(Long id, String name);

    /**
     * @Description 关闭某设备下的所有显示的地址
     * @Date 9:54 2020/8/24
     */
    void disableDisplay(String deviceId);

    /**
     * @Description 获取登录设备的显示的地址
     * @Date 9:55 2020/8/24
     */
    AddressHoldingRecordModelDto getDisplayAddress(LoginDto loginDto);

    /**
     * @Description 获取登录设备的主地址
     * @Date 9:55 2020/8/24
     */
    AddressHoldingRecordModelDto getMainAddress(LoginDto loginDto);

    AddressHoldingRecordModelDto getByAddressId(Long addressId);

    String getNameByAddressId(Long addressId);

    void changeTradePwd(String tradePassword, String newTradePassword, Long userId, boolean b);
}
