package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UserPayPayTypeEnum;
import com.liuqi.business.enums.UserPayStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.Base64Util;
import com.liuqi.utils.ValidatorUtil;
import io.shardingsphere.api.HintManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "用户")
@RestController
@RequestMapping("/front/user")//（前台）用户控制层
public class FrontUserController extends BaseFrontController {
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserPayService userPayService;
    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;

    @Autowired
    private AddressRecordService addressRecordService;

    @Autowired
    private CrowdfundRecordService crowdfundRecordService;

    /**
     * 用户基本信息
     *
     * @return
     */
    @ApiOperation(value = "用户基本信息")
    @PostMapping(value = "/baseInfo")
    @ResponseBody
    public ReturnResponse userInfo(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = this.getAddressId(request);
        return ReturnResponse.backSuccess(addressRecordService.getById(userId));
    }

    /**
     * 用户认证基本信息
     *
     * @return
     */
    @ApiOperation(value = "用户认证基本信息")
    @PostMapping(value = "/authInfo")
    @ResponseBody
    public ReturnResponse authInfo(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = this.getAddressId(request);
        UserAuthModelDto auth = userAuthService.getByUserId(userId);
        if (StringUtils.isEmpty(auth.getRemark())) {
            auth.setRemark(MessageSourceHolder.getMessage("errorMessage7"));
        }
        JSONObject obj = new JSONObject();
        obj.put("user", addressRecordService.getById(userId));
        obj.put("auth", auth);
        return ReturnResponse.backSuccess(obj);
    }
/*
    *//**
     * 前台更新用户的登录密码
     *
     * @param request
     * @param pwd
     * @param newPwd
     * @param reNewPwd
     * @param code
     * @return
     *//*
    @ApiOperation(value = "前台更新用户的登录密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "pwd", value = "旧密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "newPwd", value = "新密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "reNewPwd", value = "新确认密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "短信", required = true, paramType = "query")
    })
    @PostMapping("/changePwd")
    @ResponseBody
    public ReturnResponse changePwd(HttpServletRequest request, @RequestParam("pwd") String pwd, @RequestParam("newPwd") String newPwd,
                                    @RequestParam("reNewPwd") String reNewPwd, @RequestParam("code") String code) throws NoLoginException {
        if (StringUtils.isAnyBlank(pwd, newPwd, reNewPwd)) {
            return ReturnResponse.backFail("参数异常");
        }
        if (!StringUtils.equals(newPwd, reNewPwd)) {
            return ReturnResponse.backFail("两次输出的新密码不相等!");
        }
        if (newPwd.length() > 20 || newPwd.length() < 6) {
            return ReturnResponse.backFail("新密码输入长度有误！");
        }
        Long userId = 0L;
        addressRecordService.changePwd(userId, newPwd, pwd, false, code);
        return ReturnResponse.backSuccess();
    }*/

   /**
     * 前台更新用户的交易密码
     *
     * @param tradePassword
     * @param newTradePassword
     * @param reNewTradePassword
     * @param request
     * @return
     */
    @ApiOperation(value = "前台更新用户的交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "tradePassword", value = "旧密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "newTradePassword", value = "新密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "reNewTradePassword", value = "新确认密码", required = true, paramType = "query"),
    })
    @PostMapping(value = "/changeTradePwd")
    @ResponseBody
    public ReturnResponse changeTradePwd(@RequestParam("tradePassword") String tradePassword,
                                         @RequestParam("newTradePassword") String newTradePassword,
                                         @RequestParam("reNewTradePassword") String reNewTradePassword,
                                         HttpServletRequest request) throws NoLoginException {
        if (StringUtils.isAnyBlank(tradePassword, newTradePassword, reNewTradePassword)) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message28"));
        }
        if (!StringUtils.equals(newTradePassword, reNewTradePassword)) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("errorMessage8"));
        }
        if (newTradePassword.length() > 20 || newTradePassword.length() < 6) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("errorMessage9"));
        }
        Long userId = this.getAddressId(request);
        addressHoldingRecordService.changeTradePwd(tradePassword, newTradePassword, userId, false);
        return ReturnResponse.backSuccess();
    }


    @ApiOperation(value = "认证第一步")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "realName", value = "正实姓名", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "idcart", value = "省份证号", required = true, paramType = "query")
    })
    @PostMapping("/approveOne")
    @ResponseBody
    public ReturnResponse approveOne(HttpServletRequest request, UserAuthModel userAuthModel) throws NoLoginException {
        Long userId = this.getAddressId(request);
        if (userAuthModel == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message28"));
        }
        if (StringUtils.isAnyBlank(userAuthModel.getRealName(), userAuthModel.getIdcart())) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message28"));
        }
        userAuthService.approveOne(userId, userAuthModel);
        return ReturnResponse.backSuccess();
    }

    @ApiOperation(value = "认证第二部")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "MultipartFile", name = "file1", value = "正面", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "MultipartFile", name = "file2", value = "反面", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "MultipartFile", name = "file3", value = "手持", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "image1", value = "正面地址1", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "image2", value = "正面地址2", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "image3", value = "正面地址3", required = true, paramType = "query")
    })
    @PostMapping("/approveTwo")
    @ResponseBody
    public ReturnResponse approveTwo(@RequestParam(value = "file1", required = false) MultipartFile file1,
                                     @RequestParam(value = "file2", required = false) MultipartFile file2,
                                     @RequestParam(value = "file3", required = false) MultipartFile file3,
                                     @RequestParam(value = "image1", required = false, defaultValue = "") String image1,
                                     @RequestParam(value = "image2", required = false, defaultValue = "") String image2,
                                     @RequestParam(value = "image3", required = false, defaultValue = "") String image3,
                                     @RequestParam(value = "idcart") String idcart,
                                     @RequestParam(value = "realName") String realName,
                                     HttpServletRequest request) throws NoLoginException {
        try {
            Long userId = this.getAddressId(request);
            UserAuthModel auth = userAuthService.getByUserId(userId);
            if (userAuthService.auth(userId)) {
                return ReturnResponse.backFail(MessageSourceHolder.getMessage("message4"));
            }
            auth.setIdcart(idcart);
            auth.setRealName(realName);
            //第一个文件上传
            if ("".equals(image1)) {
                auth.setImage1(uploadFileService.uploadImg(file1, 5));
            }
            if ("".equals(image2)) {
                auth.setImage2(uploadFileService.uploadImg(file2, 5));
            }
            if ("".equals(image3)) {
                auth.setImage3(uploadFileService.uploadImg(file3, 5));
            }
            userAuthService.approveTwo(auth);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }

    }

   /* *//**
     * 找回登录密码
     *
     * @param newPwd
     * @param code
     * @param request
     * @return
     *//*
    @ApiOperation(value = "找回登录密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "newPwd", value = "新密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "短信验证码", required = true, paramType = "query")
    })
    @PostMapping("/findLoginPwd")
    @ResponseBody
    public ReturnResponse findLoginPwd(@RequestParam("newPwd") String newPwd, @RequestParam("code") String code, HttpServletRequest request) throws NoLoginException {
        if (StringUtils.isAnyBlank(newPwd, code)) {
            return ReturnResponse.backFail("参数异常");
        }
        if (newPwd.length() > 20 || newPwd.length() < 6) {
            return ReturnResponse.backFail("新密码输入长度有误！");
        }
        //获取当前用户的用户编号
        Long userId = 0L;
        userService.findLoginPwd(newPwd, code, userId);
        return ReturnResponse.backSuccess();
    }*/
/*
    *//**
     * 找回交易密码
     *
     * @param newTradePwd
     * @param code
     * @param request
     * @return
     *//*
    @ApiOperation(value = "找回交易密码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "newTradePwd", value = "新密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "短信验证码", required = true, paramType = "query")
    })
    @PostMapping("/findTradePwd")
    @ResponseBody
    public ReturnResponse findTradePwd(@RequestParam("newTradePwd") String newTradePwd, @RequestParam("code") String code, HttpServletRequest request) throws NoLoginException {
        if (StringUtils.isAnyBlank(newTradePwd, code)) {
            return ReturnResponse.backFail("参数异常");
        }
        if (newTradePwd.length() > 20 || newTradePwd.length() < 6) {
            return ReturnResponse.backFail("新密码输入长度有误！");
        }
        //获取当前用户的用户编号
        Long userId = 0L;
        userService.findTradePwd(newTradePwd, code, userId);
        return ReturnResponse.backSuccess();
    }*/
/*

    */
/**
     * 获取信息
     *
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     * @throws NoLoginException
     *//*

    @ApiOperation(value = "获取所有信息分页")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping("/myMessage")
    @ResponseBody
    public ReturnResponse myMessage(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                    @RequestParam(defaultValue = "20", required = false) final Integer pageSize, HttpServletRequest request) throws NoLoginException {
        //获取当前用户的用户编号
        Long userId = 0L;
        MessageModelDto search = new MessageModelDto();
        search.setUserId(userId);
        search.setSortName("status,t.create_time");
        PageInfo<MessageModelDto> list = messageService.queryFrontPageByDto(search, pageNum, pageSize);
        Integer count = messageService.getNotReadCount(userId);

        JSONObject obj = new JSONObject();
        obj.put("notReadCount", count);
        obj.put("list", list);
        return ReturnResponse.backSuccess(obj);
    }
*/
/*

    */
/**
     * 阅读信息
     *
     * @param request
     * @return
     * @throws NoLoginException
     *//*

    @ApiOperation(value = "阅读信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "id", value = "信息id", required = true, paramType = "query")
    })
    @PostMapping("/readMessage")
    @ResponseBody
    public ReturnResponse readMessage(@RequestParam("id") Long id, HttpServletRequest request) throws NoLoginException {
        //获取当前用户的用户编号
        Long userId = 0L;
        messageService.readMessage(userId, id);
        return ReturnResponse.backSuccess();
    }
*/


    /**
     * 获取收款列表
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取收款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping("/getPayList")
    @ResponseBody
    public ReturnResponse getPayList(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                     @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                     HttpServletRequest request) throws NoLoginException {
        Long userId = this.getAddressId(request);
        UserPayModelDto search = new UserPayModelDto();
        search.setUserId(userId);
        PageInfo<UserPayModelDto> list = userPayService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 付款设置状态
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "付款设置状态")
    @PostMapping("/toPayStatus")
    @ResponseBody
    public ReturnResponse toPayStatus(HttpServletRequest request) throws NoLoginException {
        Long userId = this.getAddressId(request);
        Map<String, Object> params = new HashMap<>();
        UserPayModelDto search = new UserPayModelDto();
        search.setUserId(userId);
        List<UserPayModelDto> payList = userPayService.queryListByDto(search, true);
        boolean yhk = false;
        boolean zfb = false;
        boolean wx = false;
        if (payList != null && payList.size() > 0) {
            for (UserPayModel pay : payList) {
                if (pay.getPayType().equals(UserPayPayTypeEnum.YHK.getCode()) && UserPayStatusEnum.USING.getCode().equals(pay.getStatus())) {
                    yhk = true;
                } else if (pay.getPayType().equals(UserPayPayTypeEnum.ZFB.getCode()) && UserPayStatusEnum.USING.getCode().equals(pay.getStatus())) {
                    zfb = true;
                } else if (pay.getPayType().equals(UserPayPayTypeEnum.WX.getCode()) && UserPayStatusEnum.USING.getCode().equals(pay.getStatus())) {
                    wx = true;
                }
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("yhk", yhk);
        obj.put("wx", wx);
        obj.put("zfb", zfb);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 获取收款信息
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "收款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "payType", value = "收款类型 1银行卡 2支付宝 3微信", required = true, paramType = "query")
    })
    @PostMapping("/payInit")
    @ResponseBody
    public ReturnResponse payInit(@RequestParam(value = "payType", defaultValue = "1") Integer payType, HttpServletRequest request) throws NoLoginException {
        //强制使用主库
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();

        //获取当前用户的用户编号
        Long userId = this.getAddressId(request);
        UserPayModelDto pay = userPayService.getByUserId(userId, payType);
        if (pay == null) {
            userPayService.init(userId, payType);
            //查询一次
            pay = userPayService.getByUserId(userId, payType);
        }
        return ReturnResponse.backSuccess(pay);
    }

    /**
     * 设置收款列表
     *
     * @param file
     * @param image
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "设置收款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "MultipartFile", name = "file1", value = "图片", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "image1", value = "图片地址", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "name", value = "姓名", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "no", value = "账号", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "bankAddress", value = "银行地址", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Stirng", name = "bankName", value = "银行名称", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "status", value = "状态", required = false, paramType = "query"),
    })
    @PostMapping("/updatePay")
    @ResponseBody
    public ReturnResponse updatePay(@RequestParam(value = "file", required = false) MultipartFile file,
                                    @RequestParam(value = "image", required = false, defaultValue = "") String image,
                                    UserPayModelDto userPay,
                                    HttpServletRequest request) throws NoLoginException {
        Long userId = this.getAddressId(request);
        UserPayModelDto pay = userPayService.getByUserId(userId, userPay.getPayType());
        //银行卡没有图片
        if (!UserPayPayTypeEnum.YHK.getCode().equals(pay.getPayType()) && "".equals(image)) {
            pay.setPic(uploadFileService.uploadImg(file, 5));
        }
        pay.setNo(userPay.getNo());
        pay.setBankAddress(userPay.getBankAddress());
        pay.setBankName(userPay.getBankName());
        pay.setName(userPay.getName());
        pay.setStatus(userPay.getStatus());
        userPayService.update(pay);
        return ReturnResponse.backSuccess();
    }

   /* *//**
     * 手机认证
     *
     * @param request
     * @param code
     * @return
     *//*
    @ApiOperation(value = "手机认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "phone", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "zone", value = "区号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query")
    })
    @PostMapping("/phoneAuth")
    @ResponseBody
    public ReturnResponse phoneAuth(HttpServletRequest request, @RequestParam("phone") String phone,
                                    @RequestParam("code") String code, @RequestParam(value = "zone", defaultValue = "86") String zone) throws NoLoginException {
        Long userId = 0L;
        if (StringUtils.isEmpty(phone) || !ValidatorUtil.isMobile(phone)) {
            return ReturnResponse.backFail("手机输入有误");
        }
        UserModel user = userService.getById(userId);
        if (user.getPhoneAuth().equals(YesNoEnum.YES.getCode())) {
            return ReturnResponse.backFail("已认证");
        }
        CodeCache.verifyCode(phone, code);

        userService.phoneAuth(userId, phone, zone);
        return ReturnResponse.backSuccess();
    }

    *//**
     * 邮箱认证
     *
     * @param request
     * @param code
     * @return
     *//*
    @ApiOperation(value = "邮箱认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "email", value = "邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query"),
    })
    @PostMapping("/emailAuth")
    @ResponseBody
    public ReturnResponse emailAuth(HttpServletRequest request, @RequestParam("email") String email, @RequestParam("code") String code) throws NoLoginException {
        Long userId = 0L;
        if (StringUtils.isEmpty(email) || !ValidatorUtil.isEmail(email)) {
            return ReturnResponse.backFail("邮箱输入有误");
        }
        UserModel user = userService.getById(userId);
        if (user.getEmailAuth().equals(YesNoEnum.YES.getCode())) {
            return ReturnResponse.backFail("已认证");
        }
        CodeCache.verifyCode(email, code);

        userService.emailAuth(userId, email);
        return ReturnResponse.backSuccess();
    }

    *//**
     * 更换认证手机
     *
     * @param request
     * @param code
     * @return
     *//*
    @ApiOperation(value = "更换认证手机")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "newPhone", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "zone", value = "区号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "uuid", value = "验证字符串", required = true, paramType = "query")
    })
    @PostMapping("/changePhone")
    @ResponseBody
    public ReturnResponse changePhone(HttpServletRequest request, @RequestParam("newPhone") String newPhone,
                                      @RequestParam("code") String code, @RequestParam("uuid") String uuid, @RequestParam(value = "zone", defaultValue = "86") String zone) throws NoLoginException {
        Long userId = 0L;
        if (StringUtils.isEmpty(newPhone) || !ValidatorUtil.isMobile(newPhone)) {
            return ReturnResponse.backFail("手机输入有误");
        }

        String key = KeyConstant.KEY_USER_VERIFY + userId;
        String uuidTemp = redisRepository.getString(key);
        if (!uuid.equals(uuidTemp)) {
            return ReturnResponse.backFail("验证已过期");
        }

        UserModel user = userService.getById(userId);
        CodeCache.verifyCode(user.getName(), code);

        userService.phoneAuth(userId, newPhone, zone);

        //验证完毕后清除
        redisRepository.del(key);
        return ReturnResponse.backSuccess();
    }

    *//**
     * 邮箱认证
     *
     * @param request
     * @param code
     * @return
     *//*
    @ApiOperation(value = "修改邮箱认证")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "newEmail", value = "邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "验证码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "uuid", value = "检测", required = true, paramType = "query")
    })
    @PostMapping("/changeEmail")
    @ResponseBody
    public ReturnResponse changeEmail(HttpServletRequest request, @RequestParam("newEmail") String newEmail,
                                      @RequestParam("code") String code, @RequestParam("uuid") String uuid) throws NoLoginException {
        Long userId = 0L;
        UserModel user = userService.getById(userId);
        if (StringUtils.isEmpty(newEmail) || !ValidatorUtil.isEmail(newEmail)) {
            return ReturnResponse.backFail("邮箱输入有误");
        }
        String key = KeyConstant.KEY_USER_VERIFY + userId;
        String uuidTemp = redisRepository.getString(key);
        if (!uuid.equals(uuidTemp)) {
            return ReturnResponse.backFail("验证已过期");
        }
        CodeCache.verifyCode(user.getName(), code);

        userService.emailAuth(userId, newEmail);
        //验证完毕后清除
        redisRepository.del(key);
        return ReturnResponse.backSuccess();
    }

    *//**
     * 修改验证码接受类型
     *
     * @param request
     * @param authType
     * @return
     *//*
    @ApiOperation(value = "修改验证码接受类型")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "authType", value = "接受方式 0手机 1邮件", required = true, paramType = "query")
    })
    @PostMapping("/changeAuthType")
    @ResponseBody
    public ReturnResponse changeAuthType(HttpServletRequest request, @RequestParam(value = "authType", defaultValue = "0") Integer authType) throws NoLoginException {
        Long userId = 0L;
        UserModel user = userService.getById(userId);
        if (authType.equals(UserModelDto.AUTHTYPE_PHONE)) {
            if (user.getPhoneAuth().equals(YesNoEnum.YES.getCode())) {
                user.setAuthType(UserModelDto.AUTHTYPE_PHONE);
            } else {
                return ReturnResponse.backFail("手机未认证");
            }
        } else {
            if (user.getEmailAuth().equals(YesNoEnum.YES.getCode())) {
                user.setAuthType(UserModelDto.AUTHTYPE_EMAIL);
            } else {
                return ReturnResponse.backFail("邮箱未认证");
            }
        }
        userService.update(user);
        return ReturnResponse.backSuccess();
    }

    *//**
     * 修改otc名称
     *
     * @param request
     * @param code
     * @return
     *//*
    @ApiOperation(value = "修改otc名称")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "name", value = "otc名称", required = true, paramType = "query"),
    })
    @PostMapping("/changeOtcName")
    @ResponseBody
    public ReturnResponse changeOtcName(HttpServletRequest request, @RequestParam("name") String name, @RequestParam("code") String code) throws NoLoginException {
        Long userId = 0L;
        UserModel user = userService.getById(userId);
        user.setOtcName(name);
        userService.update(user);
        return ReturnResponse.backSuccess();
    }

*/
    /**
     * 我的邀请
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "我的邀请")
    @PostMapping("/myInvite")
    @ResponseBody
    public ReturnResponse myInvite(HttpServletRequest request) throws NoLoginException {
        Long userId = 0L;
        //下1级
        List<Long> subList = userLevelService.getAssignSubIdList(userId, 1);
        //所有下级人数
        List<Long> teamList = userLevelService.getAllSubIdList(userId);
        JSONObject obj = new JSONObject();
        obj.put("subCount", subList != null ? subList.size() : 0);
        obj.put("teamCount", teamList != null ? teamList.size() : 0);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 邀请会员列表
     *
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "邀请会员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping(value = "/myInviteList")
    @ResponseBody
    public ReturnResponse myInviteList(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                       @RequestParam(defaultValue = "20", required = false) final Integer pageSize, HttpServletRequest request) throws NoLoginException {
        Long userId = 0L;
        UserLevelModelDto search = new UserLevelModelDto();
        search.setParentId(userId);
        PageInfo<UserLevelModelDto> page = userLevelService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(page);
    }

    @ApiOperation(value = "我的团队/共享投票")
    @PostMapping("/myTeamVote")
    public ReturnResponse myTeamVote(HttpServletRequest request,
                                     @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                                     @RequestParam(value = "currencyId",defaultValue = "2") Long currencyId){
        Long addressId = this.getAddressId(request);
        return ReturnResponse.backSuccess(crowdfundRecordService.getTeamList(addressId,currencyId,pageNum,pageSize));
    }

    @ApiOperation(value = "我的团队/共享投票数量")
    @PostMapping("/myTeamCount")
    public ReturnResponse myTeamCount(HttpServletRequest request){
        Long addressId = this.getAddressId(request);
        Map<String,Object> result = crowdfundRecordService.getTeamCount(addressId);
        return ReturnResponse.backSuccess(result);
    }

/*
    *//**
     * 我的分享地址
     *
     * @param request
     * @return
     * @throws NoLoginException
     *//*
    @ApiOperation(value = "我的分享地址")
    @PostMapping(value = "/shareAddress")
    @ResponseBody
    public ReturnResponse shareAddress(HttpServletRequest request) throws NoLoginException {
        Long userId = 0L;
        UserModel user = userService.getById(userId);
        JSONObject obj = new JSONObject();
        obj.put("address", configService.getProjectAddress() + "/register.html?leaderName=" + Base64Util.encodeToString(user.getInviteCode()));
        return ReturnResponse.backSuccess(obj);
    }*/
}
