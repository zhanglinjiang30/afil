package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.VersionModel;
import com.liuqi.business.service.AddressHoldingRecordService;
import com.liuqi.business.service.ConfigService;
import com.liuqi.business.service.VersionService;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台登录控制层
 */
@Api(description ="登录，忘记密码，退出" )
@RestController
@RequestMapping("/front")
public class LoginController extends BaseFrontController {

    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;

//    @Autowired
//    private VersionService versionService;
//    //前台登录
//    @ApiOperation(value = "登录")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType ="String",name="name" ,value = "用户名",required = true,paramType = "query"),
//            @ApiImplicitParam(dataType ="String",name="password" ,value = "密码",required = true,paramType = "query")
//    })
//    @PostMapping(value = "/loginNV")
//    public ReturnResponse loginNV(@RequestParam("name") String name, @RequestParam("password") String password,
//                                  HttpServletRequest request, ModelMap modelMap) {
//        Map<String,Object> mpa=userService.login(name, password, request);
//        return ReturnResponse.backSuccess(mpa);
//    }
//
//    //前台登录
//    @ApiOperation(value = "登录")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType ="String",name="name" ,value = "用户名",required = true,paramType = "query"),
//            @ApiImplicitParam(dataType ="String",name="password" ,value = "密码",required = true,paramType = "query"),
//            @ApiImplicitParam(dataType ="String",name="version" ,value = "版本",required = true,paramType = "query"),
//            @ApiImplicitParam(dataType = "String", name = "type", value = "类型0安卓 1ios", required = true, paramType = "query"),
//    })
//    @PostMapping(value = "/login")
//    public ReturnResponse login(@RequestParam("name") String name, @RequestParam("password") String password,
//                                @RequestParam("version") String version,
//                                @RequestParam(value = "type", defaultValue = "0") Integer type,
//                                HttpServletRequest request, ModelMap modelMap) {
//        VersionModel versionModel = versionService.getConfig();
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        //是否必须升级
//        boolean mustUpdate = !versionModel.getAndroidVersion().equalsIgnoreCase(version);
//        String appAddress = versionModel.getAndroidAddress();
//        String appVersion = versionModel.getAndroidVersion();
//        String updateInfo = versionModel.getAndroidInfo();
//        if (type == 1) {//ios
//            mustUpdate = !versionModel.getIosVersion().equalsIgnoreCase(version);
//            appAddress = versionModel.getIosAddress();
//            appVersion = versionModel.getIosVersion();
//            updateInfo = versionModel.getIosInfo();
//        }
//        if (mustUpdate) {
//            map.put("appAddress", appAddress);
//            map.put("appVersion", appVersion);
//            map.put("updateInfo", updateInfo);
//            return ReturnResponse.backInfo(ReturnResponse.RETURN_UPDATE, "更新", map);
//        }
//        Map<String,Object> mpa=userService.login(name, password, request);
//        return ReturnResponse.backSuccess(mpa);
//    }
//
//    //忘记密码
//    @ApiOperation(value = "忘记密码")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType ="String",name="name" ,value = "用户名",required = true,paramType = "query"),
//            @ApiImplicitParam(dataType ="String",name="pwd" ,value = "密码",required = true,paramType = "query"),
//            @ApiImplicitParam(dataType ="String",name="code" ,value = "短信",required = true,paramType = "query")
//    })
//    @PostMapping(value = "/forgetPassword")
//    public ReturnResponse forgetPassword(@RequestParam("name") String name, @RequestParam("pwd") String pwd, @RequestParam("code") String code) {
//        userService.forgetPassword(name,pwd,code);
//        return ReturnResponse.backSuccess();
//    }
//
    @ApiOperation(value = "退出")
    @PostMapping(value = "/logout")
    public ReturnResponse logout(HttpServletRequest request){
        LoginDto loginDto = getLoginDto(request);
        LoginUserTokenHelper.removeUser(request);
        // 清除此设备持有的所有地址
        addressHoldingRecordService.deleteByDeviceId(loginDto.getDeviceId());
        return ReturnResponse.backSuccess();
    }
}
