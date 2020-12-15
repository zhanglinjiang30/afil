package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.AddressHoldingRecordModelDto;
import com.liuqi.business.model.AddressRecordModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 助记词管理
 */
@Api(description = "助记词")
@RequestMapping("/front/address")
@RestController
public class FrontAddressController extends BaseFrontController {

    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;
    @Autowired
    private PassphraseDeviceService passphraseDeviceService;
    @Autowired
    private AirdropRecordService airdropRecordService;
    @Autowired
    private AddressRecordService addressRecordService;

    @ApiOperation(value = "获取地址")
    @PostMapping("/get/list")
    public ReturnResponse getPassphrase(HttpServletRequest request) throws NoLoginException {
        List<AddressHoldingRecordModelDto> list = addressHoldingRecordService.getAddressesByDeviceId(getLoginDto(request));
        return ReturnResponse.backSuccess(list);
    }

    @ApiOperation(value = "设置显示地址")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="id" ,value = "持有记录id",paramType = "query"),
    })
    @PostMapping("/set/display")
    public ReturnResponse setDisplay(HttpServletRequest request, Long id) throws NoLoginException {
        return addressHoldingRecordService.setDisplay(getLoginDto(request), id);
    }

    @ApiOperation(value = "修改钱包名称")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="id" ,value = "持有记录id",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="name" ,value = "钱包名称",paramType = "query")
    })
    @PostMapping("/modify/name")
    public ReturnResponse modifyAddressName(HttpServletRequest request, Long id, String name) throws NoLoginException {
        return addressHoldingRecordService.modifyAddressName(id, name);
    }

    @ApiOperation(value = "导入地址-通过助记词")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="passphrase" ,value = "助记词",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="name" ,value = "钱包名称",paramType = "query")
    })
    @PostMapping("/import/bypassphrase")
    public ReturnResponse importAddressByPassphrase(String passphrase, String name, HttpServletRequest request) throws NoLoginException {
        return passphraseDeviceService.importAddressByPassphrase(getLoginDto(request), passphrase, name);
    }

    @ApiOperation(value = "导入地址-通过私钥")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="privateKey" ,value = "私钥",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="name" ,value = "钱包名称",paramType = "query")
    })
    @PostMapping("/import/byprivatekey")
    public ReturnResponse importAddressByPrivateKey(String privateKey, String name, HttpServletRequest request) throws NoLoginException {
        return passphraseDeviceService.importAddressByPrivateKey(getLoginDto(request), privateKey, name);
    }

    @ApiOperation(value = "创建地址")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="name" ,value = "钱包名称",paramType = "query")
    })
    @PostMapping("/create/do")
    public ReturnResponse doPassphraseImport(String name, HttpServletRequest request) throws NoLoginException {
        return passphraseDeviceService.createAddress(getLoginDto(request), name);
    }

    @ApiOperation(value = "获取当前显示的地址")
    @PostMapping("/get/display")
    public ReturnResponse getDisplayAddress(HttpServletRequest request) throws NoLoginException {
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(getLoginDto(request));
        return a == null ? ReturnResponse.backFail(MessageSourceHolder.getMessage("message26")) : ReturnResponse.backSuccess(a);
    }

   /* @ApiOperation(value = "地址空投")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="BigDecimal",name="amount" ,value = "空投数量",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="tradePwd" ,value = "交易密码",paramType = "query")
    })
    @PostMapping("/airdrop/do")
    public ReturnResponse importAddressByPrivateKey(BigDecimal amount, String tradePwd, HttpServletRequest request) throws NoLoginException {
        return airdropRecordService.doAirDrop(getLoginDto(request), tradePwd, amount);
    }*/

    @ApiOperation(value = "查询当前显示的地址是否激活")
    @PostMapping("/query/active")
    public ReturnResponse queryActiveStatus(HttpServletRequest request) throws NoLoginException {
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(getLoginDto(request));
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }
        AddressRecordModelDto aa = addressRecordService.getById(a.getAddressId(), false);
        return ReturnResponse.backSuccess(aa == null ? 0 : aa.getActive());
    }

    @ApiOperation(value = "搜索钱包名称")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="name" ,value = "钱包名称",paramType = "query")
    })
    @PostMapping("/do/search")
    public ReturnResponse doWalletSearch(String name, HttpServletRequest request) throws NoLoginException {
        return ReturnResponse.backSuccess(addressHoldingRecordService.getAddressByLikeName(getLoginDto(request), name));
    }

    @ApiOperation(value = "执行激活")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="tradePwd" ,value = "交易密码",paramType = "query")
    })
    @PostMapping("/do/active")
    public ReturnResponse doActive(String tradePwd, HttpServletRequest request) throws NoLoginException {
        return addressRecordService.doActive(getLoginDto(request), tradePwd);
    }

    @ApiOperation(value = "激活地址")
    @PostMapping("activeAddress")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="toAddress" ,value = "接收方地址",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="tradePwd" ,value = "交易密码",paramType = "query")
    })
    public ReturnResponse activeAddress(HttpServletRequest request,String toAddress,String tradePwd){
        addressRecordService.activeAddress(getLoginDto(request),toAddress,tradePwd);
        return ReturnResponse.backSuccess();
    }
}

