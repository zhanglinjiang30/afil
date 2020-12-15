package com.liuqi.business.controller.front;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.enums.HelpStatusEnum;
import com.liuqi.business.model.HelpModelDto;
import com.liuqi.business.model.HelpTypeModelDto;
import com.liuqi.business.service.HelpService;
import com.liuqi.business.service.HelpTypeService;
import com.liuqi.business.service.PassphraseDeviceService;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.bitcoinj.crypto.MnemonicCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;

/**
 * 助记词管理
 */
@Api(description = "助记词")
@RequestMapping("/front/passphrase")
@RestController
public class FrontPassphraseController extends BaseFrontController {

    @Autowired
    private PassphraseDeviceService passphraseDeviceService;

    @ApiOperation(value = "获取助记词")
    @PostMapping("/first/get")
    public ReturnResponse getPassphrase() throws NoLoginException {
        return passphraseDeviceService.getNewPassphrase();
    }

    @ApiOperation(value = "备份助记词")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="passphrase" ,value = "助记词",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="deviceId" ,value = "设备号",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="name" ,value = "钱包名称",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="tradePwd" ,value = "交易密码",paramType = "query")
    })
    @PostMapping("/backup/do")
    public ReturnResponse doPassphraseBackup(String passphrase, String deviceId, String name, String tradePwd) throws NoLoginException {
        return passphraseDeviceService.doPassphraseBackup(passphrase, deviceId, name, tradePwd);
    }

    @ApiOperation(value = "导入助记词")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="passphrase" ,value = "助记词",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="deviceId" ,value = "设备号",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="name" ,value = "钱包名称",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="tradePwd" ,value = "交易密码",paramType = "query")
    })
    @PostMapping("/import/do")
    public ReturnResponse doPassphraseImport(String passphrase, String deviceId, String name, String tradePwd) throws NoLoginException {
        return passphraseDeviceService.importPassphrase(passphrase, deviceId, name, tradePwd);
    }

}

