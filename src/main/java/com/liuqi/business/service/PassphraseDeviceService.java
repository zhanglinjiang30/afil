package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.PassphraseDeviceModel;
import com.liuqi.business.model.PassphraseDeviceModelDto;
import com.liuqi.response.ReturnResponse;

import java.util.List;

public interface PassphraseDeviceService extends BaseService<PassphraseDeviceModel,PassphraseDeviceModelDto>{

    /**
     * @Description 获取新助记词
     * @Date 16:11 2020/8/9
     */
    ReturnResponse getNewPassphrase();

    /**
     * @Description 备份助记词
     * @Date 18:22 2020/8/9
     */
    ReturnResponse doPassphraseBackup(String passphrase, String deviceId, String name, String tradePwd);

    /**
     * @Description 导入助记词
     * @Date 21:58 2020/8/9
     */
    ReturnResponse importPassphrase(String passphrase, String deviceId, String name, String tradePwd);

    /**
     * @Description 导入地址 - 通过助记词
     * @Date 21:58 2020/8/9
     */
    ReturnResponse importAddressByPassphrase(LoginDto loginDto, String passphrase, String name);

    /**
     * @Description 导入地址 - 通过私钥
     * @Date 21:58 2020/8/9
     */
    ReturnResponse importAddressByPrivateKey(LoginDto loginDto, String privateKey, String name);

    /**
     * @Description 创建地址
     * @Date 15:47 2020/8/14
     */
    ReturnResponse createAddress(LoginDto loginDto, String name);

    ReturnResponse getActiveCount(LoginDto loginDto);

}
