package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.AssetDto;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import com.liuqi.utils.ShiroPasswdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

/**
 * 财务中心
 */
@Api(description = "财务中心")
@RequestMapping("/front/financial")
@RestController
public class FrontFinancialController extends BaseFrontController {
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private UserWalletAddressService userWalletAddressService;
    @Autowired
    private UserWalletLogService userWalletLogService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private ExtractService extractService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserRechargeAddressService userRechargeAddressService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private SearchPrice searchPrice;
    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;
    @Autowired
    private AddressRecordService addressRecordService;

    @Autowired
    private WalletTransferService walletTransferService;

    @Autowired
    private UserPoolWalletService userPoolWalletService;

    @Autowired
    private UserOtcWalletService userOtcWalletService;

    @Autowired
    private AirdropRecordService airdropRecordService;

    @Autowired
    private UserOtcWalletLogService userOtcWalletLogService;

    @Autowired
    private UserPoolWalletLogService userPoolWalletLogService;



    @ApiOperation(value = "获取所有资产种")
    @PostMapping("/assetsTotal")
    public ReturnResponse assetsTotal(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = this.getAddressId(request);

        AssetDto userWallet = userWalletService.getTotalWallet(userId);
        AssetDto otcWallet = userOtcWalletService.getTotalWallet(userId);
        AssetDto poolWallet = userPoolWalletService.getTotalWallet(userId);

        Map<String, Object> map = new HashedMap();
        map.put("usdt",MathUtil.add(userWallet.getTotal(), otcWallet.getTotal()).add(poolWallet.getTotal()));
        map.put("cny", MathUtil.add(userWallet.getUsdtTotal(), otcWallet.getUsdtTotal()).add(poolWallet.getUsdtTotal()));
        return ReturnResponse.backSuccess(map);
    }

    /**
     * 获取资产
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取资产,(可根据币种名称过滤)")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型 1 币币, 法币, 3 矿池", required = true, paramType = "query")
    })
    @PostMapping("/assets")
    public ReturnResponse assets(HttpServletRequest request,Integer type) throws NoLoginException {
        Long addressId = this.getAddressId(request);

        AssetDto assetDto = new AssetDto();
        if (UserWalletTypeEnum.USING.getCode().equals(type)){
            assetDto= userWalletService.getTotalWallet(addressId);
        }else if (UserWalletTypeEnum.CURRENCY.getCode().equals(type)){
            assetDto = userOtcWalletService.getTotalWallet(addressId);
        }else if (UserWalletTypeEnum.POOL.getCode().equals(type)){
            assetDto = userPoolWalletService.getTotalWallet(addressId);
        }

        return ReturnResponse.backSuccess(assetDto);
    }

    /**
     * 获取可用资产
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取指定资产")
    @PostMapping("/assetsByCurrency")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "assetType", value = "资产类型 1 币币, 法币, 3 矿池", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query")
    })
    public ReturnResponse assetsByCurrency(@RequestParam("assetType") Integer assetType,
                                           @RequestParam("currencyId") Long currencyId, HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getAddressId(request);
        BigDecimal usdtQc = searchPrice.getUsdtQcPrice();

        BigDecimal price = searchPrice.getPrice(currencyService.getNameById(currencyId));

        JSONObject obj = new JSONObject();
        if (UserWalletTypeEnum.USING.getCode().equals(assetType)) {
            UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(userId, currencyId);
            wallet.setCny(MathUtil.mul(MathUtil.mul(MathUtil.add(wallet.getUsing(),wallet.getFreeze()),usdtQc),price));
            wallet.setCurrencyName(currencyService.getNameById(wallet.getCurrencyId()));
            obj.put("wallet", wallet);
            return ReturnResponse.backSuccess(obj);
        } else if (UserWalletTypeEnum.CURRENCY.getCode().equals(assetType)) {
            UserOtcWalletModelDto wallet = userOtcWalletService.getByUserAndCurrencyId(userId, currencyId);
            wallet.setCny(MathUtil.mul(MathUtil.mul(MathUtil.add(wallet.getUsing(),wallet.getFreeze()),usdtQc),price));
            wallet.setCurrencyName(currencyService.getNameById(wallet.getCurrencyId()));
            obj.put("wallet", wallet);
            return ReturnResponse.backSuccess(obj);
        } else if (UserWalletTypeEnum.POOL.getCode().equals(assetType)) {
            UserPoolWalletModelDto wallet = userPoolWalletService.getByUserAndCurrencyId(userId, currencyId);
            wallet.setCny(MathUtil.mul(MathUtil.mul(MathUtil.add(wallet.getUsing(),wallet.getFreeze()),usdtQc),price));
            wallet.setCurrencyName(currencyService.getNameById(wallet.getCurrencyId()));
            obj.put("wallet", wallet);
            return ReturnResponse.backSuccess(obj);
        }
        return ReturnResponse.backFail(MessageSourceHolder.getMessage("message27"));
    }

    @ApiOperation(value = "根据币种获取余额")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query")
    })
    @PostMapping("/get/assets")
    public ReturnResponse getAssets(HttpServletRequest request, Long currencyId) throws NoLoginException {
        LoginDto loginDto = super.getLoginDto(request);
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }
        UserWalletModelDto userWalletModelDto = userWalletService.getByUserAndCurrencyId(a.getAddressId(), currencyId);
        return ReturnResponse.backSuccess(userWalletModelDto == null ? BigDecimal.ZERO : userWalletModelDto.getUsing());
    }

    /**
     * 获取充币地址
     */
    @ApiOperation(value = "获取充值地址")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query"),
    })
    @PostMapping("/rechargeInit")
    public ReturnResponse rechargeInit(HttpServletRequest request, Long currencyId) throws NoLoginException {
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(getLoginDto(request));
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }
        Long usdtId = currencyService.getUsdtId();
        CurrencyModel currencyModel = currencyService.getById(currencyId);
        //获取充值地址
        String address = this.getAddress(a.getAddressId(), currencyModel.getProtocol());
        CurrencyConfigModel config = currencyConfigService.getByCurrencyId(currencyId);
        JSONObject obj = new JSONObject();
        obj.put("switch", SwitchEnum.isOn(config.getRechargeSwitch()));//充值开关
        obj.put("min", config.getRechargeMinQuantity());//充值最小数量
        obj.put("memo", "");//标签
        obj.put("address", address);//充值地址
        obj.put("twoAddress", false);//是否有2个协议
        if (usdtId.equals(currencyId) && currencyModel.getProtocol2() > 0 && StringUtils.isNotEmpty(currencyModel.getThirdCurrency2())) {
            obj.put("twoAddress", true);//充值地址
            JSONArray array = new JSONArray();
            JSONObject show = new JSONObject();
            //协议1的地址
            show.put(ProtocolEnum.getShow(currencyModel.getProtocol()), address);
            array.add(show);

            //协议2的地址
            JSONObject show2 = new JSONObject();
            address = this.getAddress(a.getAddressId(), currencyModel.getProtocol2());
            show2.put(ProtocolEnum.getShow(currencyModel.getProtocol2()), address);
            array.add(show2);
            obj.put("list", array);
        }
        if (ProtocolEnum.EOS.getCode().equals(currencyModel.getProtocol())
                || ProtocolEnum.XRP.getCode().equals(currencyModel.getProtocol())) {
            obj.put("memo", address);//标签
            obj.put("address", config.getRechargeAddress());//充值地址
        }
        return ReturnResponse.backSuccess(obj);
    }

    private String getAddress(Long userId, Integer protocol) {
        //协议2的地址
        String address = userRechargeAddressService.getRechargeAddress(userId, protocol);
        if (StringUtils.isEmpty(address)) {
            address = userRechargeAddressService.initRechargeAddressLock(userId, protocol);
        }
        return address;
    }

    /**
     * 查询充币记录
     *
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取币种充值信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping("/rechargeList")
    public ReturnResponse rechargeList(HttpServletRequest request, @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                       @RequestParam(defaultValue = "20", required = false) final Integer pageSize) throws NoLoginException {
        RechargeModelDto search = new RechargeModelDto();
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(getLoginDto(request));
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }
        search.setUserId(a.getAddressId());
        //查询会员充币记录
        PageInfo<RechargeModelDto> pageInfo = rechargeService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }

    /**
     * 查询提币手续费地址
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询提币手续费地址")

    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query")
    })
    @PostMapping("/extractInit")
    public ReturnResponse extractInit(HttpServletRequest request, @RequestParam("currencyId") Long currencyId) throws NoLoginException {
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(getLoginDto(request));
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }
        //查询提币手续费
        CurrencyConfigModel config = currencyConfigService.getByCurrencyId(currencyId);
        String poundage = config.getExtractRate().doubleValue() + "";
        // 查询提币地址
        UserWalletAddressModelDto search = new UserWalletAddressModelDto();
        search.setUserId(a.getAddressId());
        search.setCurrencyId(currencyId);
        List<UserWalletAddressModelDto> list = userWalletAddressService.queryListByDto(search, true);

        UserWalletModel userWallet = userWalletService.getByUserAndCurrencyId(a.getAddressId(), currencyId);

        Map modelMap = new HashMap();
        modelMap.put("switch", SwitchEnum.isOn(config.getExtractSwitch()));//开关
        modelMap.put("rate", poundage);//手续费
        modelMap.put("percentage", YesNoEnum.YES.getCode().equals(config.getPercentage()));//是否百分比
        modelMap.put("miner", getExtractFee());// 矿工费
        modelMap.put("gateWayAddress", configService.queryValueByName(ConfigConstant.CONFIG_GATEWAY_ADDRESS));// 网关地址
        modelMap.put("min", config.getExtractMin());//最小数量
        modelMap.put("max", config.getExtractMax());//最大数量
        modelMap.put("day", config.getExtractMaxDay());//每天最大数量
        modelMap.put("daySwitch", SwitchEnum.isOn(config.getExtractMaxDaySwitch()));//每天最大值开关
        modelMap.put("using", userWallet.getUsing());//可用数量
        modelMap.put("addressList", list);//地址
        return ReturnResponse.backSuccess(modelMap);
    }

    private BigDecimal getExtractFee() {
        String value = configService.queryValueByName(ConfigConstant.CONFIG_EXTRACT_MINER_FEE);
        return StringUtils.isEmpty(value) ? BigDecimal.valueOf(0.001) : new BigDecimal(value);
    }

    /**
     * 提币申请
     *
     * @param extractCoinRecord
     * @param request
     * @return
     */
    @ApiOperation(value = "提币申请")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "address", value = "地址", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "memo", value = "eos标签", paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "tradePwd", value = "交易密码", paramType = "query")
    })
    @PostMapping("/apply")
    @ResponseBody
    public ReturnResponse apply(@Valid ExtractModel extractCoinRecord, String tradePwd, BindingResult bindingResult,
                                HttpServletRequest request) throws NoLoginException {
        LoginDto loginDto = getLoginDto(request);
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }
        Long addressId = a.getAddressId();
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message28")+":" + getErrorInfo(bindingResult));
        }
        if (extractCoinRecord.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("errorMessage4"));
        }
        //去除空格
        extractCoinRecord.setAddress(extractCoinRecord.getAddress().trim());
        extractCoinRecord.setMemo(StringUtils.isNotEmpty(extractCoinRecord.getMemo()) ? extractCoinRecord.getMemo().trim() : "");

        CurrencyConfigModel config = currencyConfigService.getByCurrencyId(extractCoinRecord.getCurrencyId());
        if (!SwitchEnum.isOn(config.getExtractSwitch())) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message29"));
        }
        if (config.getExtractMin().compareTo(BigDecimal.ZERO) > 0 && config.getExtractMin().compareTo(extractCoinRecord.getQuantity()) > 0) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message17") + config.getExtractMin());
        }
        if (config.getExtractMax().compareTo(BigDecimal.ZERO) > 0 && config.getExtractMax().compareTo(extractCoinRecord.getQuantity()) < 0) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message18") + config.getExtractMax());
        }
        AddressHoldingRecordModelDto mainHoldingAddress = addressHoldingRecordService.getMainAddress(loginDto);
        if (mainHoldingAddress == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message30"));
        }
        if (!StringUtils.equals(mainHoldingAddress.getTradePwd(), ShiroPasswdUtil.getUserPwd(tradePwd))) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("errorMessage5"));
        }
        String key = LockConstant.LOCK_EXTRACT_ORDER_USER + addressId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            extractCoinRecord.setUserId(addressId);
            extractService.extractApply(extractCoinRecord, config);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 地址管理
     *
     * @param request
     * @param currencyId
     * @return
     */
    @ApiOperation(value = "获取设置的提现地址")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "addressId", value = "地址id", required = true, paramType = "query")
    })
    @PostMapping("/walletAddressList")
    public ReturnResponse walletAddressList(HttpServletRequest request, Long currencyId, Long addressId) throws NoLoginException {
        UserWalletAddressModelDto search = new UserWalletAddressModelDto();
        search.setUserId(addressId);
        search.setCurrencyId(currencyId);
        List<UserWalletAddressModelDto> list = userWalletAddressService.queryListByDto(search, true);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 添加提币地址
     *
     * @param remark
     * @param currencyId
     * @param address
     * @param request
     * @return
     */
    @ApiOperation(value = "添加提现地址")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "remark", value = "备注", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "address", value = "提币地址", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "memo", value = "标签", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "addressId", value = "地址id", required = true, paramType = "query")
    })
    @PostMapping("/addAddress")
    public ReturnResponse addAddress(@RequestParam("remark") String remark, @RequestParam("currencyId") Long currencyId,
                                     @RequestParam("address") String address, @RequestParam(value = "memo", required = false, defaultValue = "") String memo,
                                     Long addressId, HttpServletRequest request) throws NoLoginException {
        if (StringUtils.isAnyBlank(remark, address) || currencyId == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message28"));
        }
        address = address.trim();
        memo = memo.trim();

        userWalletAddressService.addAddress(remark, currencyId, address, memo, addressId);
        return ReturnResponse.backSuccess();
    }

    /**
     * 删除提币地址
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除提现地址")
    @ApiImplicitParam(dataType = "Long", name = "id", value = "id", required = true, paramType = "query")
    @PostMapping("/deleteAddress")
    public ReturnResponse deleteAddress(@RequestParam("id") Long id) {
        boolean flag = userWalletAddressService.delete(id);
        if (flag) {
            return ReturnResponse.backSuccess();
        } else {
            return ReturnResponse.backFail();
        }
    }


    /**
     * 查询提币记录
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询提币记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/extractList")
    public ReturnResponse extractList(HttpServletRequest request, ModelMap modelMap,
                                      @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                      @RequestParam(defaultValue = "20", required = false) final Integer pageSize) throws NoLoginException {
        ExtractModelDto search = new ExtractModelDto();
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(getLoginDto(request));
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }
        search.setUserId(a.getAddressId());
        PageInfo<ExtractModelDto> pageInfo = extractService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }


    /**
     * 获取账单记录类型
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取账单记录类型")
    @PostMapping("/getLogType")
    public ReturnResponse getLogType(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        List<SelectDto> list = WalletLogTypeEnum.getList();
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 获取账单
     */
    @ApiOperation(value = "获取账单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Date", name = "startDate", value = "开始时间", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Date", name = "endDate", value = "结束时间", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型", required = false, paramType = "query"),
    })
    @PostMapping("/getLog")
    public ReturnResponse getLog(@RequestParam(value = "currencyId", required = false, defaultValue = "-2") Long currencyId,
                                 @RequestParam(value = "type", required = false) Integer type,
                                 @RequestParam(value = "startDate", required = false, defaultValue = "") Date startDate,
                                 @RequestParam(value = "endDate", required = false, defaultValue = "") Date endDate,
                                 @RequestParam(value = "pageNum", defaultValue = "1", required = false) final Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) final Integer pageSize,
                                 HttpServletRequest request) throws NoLoginException {
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(getLoginDto(request));
        if (a == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message26"));
        }

        if (UserWalletTypeEnum.USING.getCode().equals(type)) {
            UserWalletLogModelDto search = new UserWalletLogModelDto();
            search.setUserId(a.getAddressId());
            search.setCurrencyId(currencyId);
//            search.setType(type);
            search.setStartCreateTime(startDate);
            search.setEndCreateTime(endDate);
            PageInfo<UserWalletLogModelDto> page = userWalletLogService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(page);
        } else if (UserWalletTypeEnum.CURRENCY.getCode().equals(type)) {
            UserOtcWalletLogModelDto search = new UserOtcWalletLogModelDto();
            search.setUserId(a.getAddressId());
            search.setCurrencyId(currencyId);
//            search.setType(type);
            search.setStartCreateTime(startDate);
            search.setEndCreateTime(endDate);
            PageInfo<UserOtcWalletLogModelDto> page = userOtcWalletLogService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(page);
        } else if (UserWalletTypeEnum.POOL.getCode().equals(type)) {
            UserPoolWalletLogModelDto search = new UserPoolWalletLogModelDto();
            search.setUserId(a.getAddressId());
            search.setCurrencyId(currencyId);
//            search.setType(type);
            search.setStartCreateTime(startDate);
            search.setEndCreateTime(endDate);
            PageInfo<UserPoolWalletLogModelDto> page = userPoolWalletLogService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(page);
        }

        return ReturnResponse.backFail("error");
    }

    @ApiOperation(value = "获取充提记录明细")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型1:充值 2提现", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "id", value = "id", required = true, paramType = "query")
    })
    @PostMapping("/detailInfo")
    public ReturnResponse detailInfo(HttpServletRequest request, @RequestParam(value = "type", defaultValue = "1") Integer type, @RequestParam("id") Long id) throws NoLoginException {
        if (1 == type) {
            //查询会员充币记录
            RechargeModel recharge = rechargeService.getById(id);
            return ReturnResponse.backSuccess(recharge);
        } else {
            ExtractModel extract = extractService.getById(id);
            return ReturnResponse.backSuccess(extract);
        }
    }

    @ApiOperation(value = "钱包列表")
    @PostMapping("getWalletList")
    public ReturnResponse getWalletList(HttpServletRequest request){
        return ReturnResponse.backSuccess(UserWalletTypeEnum.getList());
    }

    @ApiOperation(value = "资产划转")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "sourceId", value = "被划转钱包", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "targetId", value = "指定钱包", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "划转数量", required = true, paramType = "query"),
    })
    @PostMapping("transfer")
    public ReturnResponse transfer(HttpServletRequest request, Integer sourceId, Integer targetId, Long currencyId, BigDecimal quantity) {
        Long userId = this.getAddressId(request);
        walletTransferService.transfer(userId, sourceId, targetId, currencyId, quantity);
        return ReturnResponse.backSuccess();
    }

    @ApiOperation(value = "划转记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = true, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "当前数量", required = true, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("transferRecord")
    public ReturnResponse transferRecord(HttpServletRequest request,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        WalletTransferModelDto search = new WalletTransferModelDto();
        Long userId= this.getAddressId(request);
        search.setUserId(userId);
        return ReturnResponse.backSuccess(walletTransferService.queryFrontPageByDto(search, pageNum, pageSize));
    }

    @ApiOperation(value = "购买空投")
    @PostMapping("doAirDrop")
    public ReturnResponse doAirDrop(HttpServletRequest request,String tradePwd,BigDecimal quantity){
        return airdropRecordService.doAirDrop(this.getLoginDto(request),tradePwd,quantity);
    }

    @ApiOperation(value = "空投记录")
    @PostMapping("airDropRecord")
    public ReturnResponse airDropRecord(HttpServletRequest request,String tradePwd,BigDecimal quantity){
        return airdropRecordService.doAirDrop(this.getLoginDto(request),tradePwd,quantity);
    }
}

