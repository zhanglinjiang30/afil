package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.SlideTypeEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.mapper.ActiveRecordMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.Captcha;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 前台登录控制层
 */
@Api(description ="首页（不验证登录状态）" )
@RestController
@RequestMapping("/search")
public class indexController extends BaseFrontController {
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private SlideService slideService;
    @Autowired
    private IndexService indexService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private InformationService informationService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private SearchPrice searchPrice;
    @Autowired
    private CoinArticleService coinArticleService;
    @Autowired
    private ActiveRecordService activeRecordService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AddressRecordService addressRecordService;

    /**
     *
     * @param request
     * @param response
     * @param lang
     * @return
     */
    @ApiOperation(value = "切换语言")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="lang" ,value = "语言 zh：中文  en:英文",required = true,paramType = "query")
    })
    @PostMapping("/changeLanauage")
    public ReturnResponse changeLanauage(HttpServletRequest request, HttpServletResponse response, @RequestParam("lang") String lang){
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if("zh".equals(lang)){
            localeResolver.setLocale(request, response, new Locale("zh","CN"));
        }else if("en".equals(lang)){
            localeResolver.setLocale(request, response, new Locale("en","US"));
        }
        request.getSession().setAttribute("lanauage",lang);
        return ReturnResponse.backSuccess();
    }

    /**
     * 查询公告
     * @return
     */
    @ApiOperation(value = "查询公告")
    @PostMapping(value = "/indexContent")
    public ReturnResponse indexContent() {
        List<ContentModelDto> contentList=contentService.getNewContent(2);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("contentList",contentList);
        return ReturnResponse.backSuccess(map);
    }

    @ApiOperation(value = "查询公告")
    @GetMapping(value = "/refresh")
    public ReturnResponse refresh() {
        return activeRecordService.refreshActiveData();
    }


    /**
     * 查询pc轮播图
     * @return
     */
    @ApiOperation(value = "查询轮播图")
    @PostMapping(value = "/indexSlidesPC")
    public ReturnResponse indexSlidesPC() {
        List<SlideModelDto> slideList = slideService.findSlide(SlideTypeEnum.PC.getCode());
        return ReturnResponse.backSuccess(slideList);
    }

    /**
     * 查询app轮播图
     * @return
     */
    @ApiOperation(value = "查询轮播图")
    @PostMapping(value = "/indexSlides")
    public ReturnResponse indexSlides() {
        List<SlideModelDto> slideList = slideService.findSlide(SlideTypeEnum.APP.getCode());
        return ReturnResponse.backSuccess(slideList);
    }

    /**
     * 查询区域
     *
     * @return
     */
    @ApiOperation(value = "查询区域")
    @PostMapping(value = "/indexArea")
    public ReturnResponse indexArea() {
        List<CurrencyAreaModelDto> dtoList = currencyAreaService.findAllCanUseArea();
        return ReturnResponse.backSuccess(dtoList);
    }
    /**
     * 查询常用交易对
     * @return
     */
    @ApiOperation(value = "查询常用交易对")
    @PostMapping(value = "/indexCommTrade")
    public ReturnResponse indexCommTrade() {
        List<TradeInfoDto> commTradeList=indexService.indexCommTrade();
        return ReturnResponse.backSuccess(commTradeList);
    }

    @ApiOperation(value = "充提币开关")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "1=提币，2=充币",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种id",required = true,paramType = "query")
    })
    @PostMapping(value = "/switch")
    public ReturnResponse getSwitch(Integer type, Long currencyId) {
        CurrencyConfigModelDto c = currencyConfigService.getByCurrencyId(currencyId);
        return ReturnResponse.backSuccess(type.compareTo(1) == 0 ? c.getExtractSwitch() : c.getRechargeSwitch()) ;
    }

    /**
     * 查询区域交易对
     * @param areaId
     * @return
     */
    @ApiOperation(value = "查询区域交易对")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="areaId" ,value = "区域id",required = true,paramType = "query")
    })
    @PostMapping(value = "/indexTrade")
    public ReturnResponse indexTrade(@RequestParam("areaId")Long areaId) {
        List<TradeInfoDto> dtoList = indexService.getByAreaId(areaId);
        return ReturnResponse.backSuccess(dtoList);
    }


    //获取交易对币种信息
    @ApiOperation(value = "获取交易对币种信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "tradeId", value = "交易对id", required = true, paramType = "query")
    })
    @ResponseBody
    @PostMapping("/tradeInfo")
    public ReturnResponse getTradeInfo(@RequestParam("tradeId") Long tradeId) {
        CurrencyTradeModelDto trade = currencyTradeService.getById(tradeId);
        JSONObject json = new JSONObject();
        if(trade!=null) {
            json.put("currencyId", trade.getCurrencyId());
            json.put("currencyTradeId", trade.getTradeCurrencyId());
            json.put("currencyName", trade.getCurrencyName());
            json.put("currencyTradeName", trade.getTradeCurrencyName());
            json.put("tradeId", tradeId);
            List<CurrencyAreaModelDto> dtoList = currencyAreaService.findAllCanUseArea();
            json.put("areaList", dtoList);
        }
        return ReturnResponse.backSuccess(json);
    }

    /**
     * 交易信息
     *
     * @param
     * @return
     */
    @ApiOperation(value = "交易信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "tradeId", value = "交易对id", required = true, paramType = "query")
    })
    @PostMapping(value = "/trade")
    @ResponseBody
    public ReturnResponse trade(@RequestParam("tradeId") Long tradeId, HttpServletRequest request) {
        CurrencyTradeModelDto trade = currencyTradeService.getById(tradeId);
        if (trade != null) {
            TradeInfoDto dto = tradeService.getByCurrencyAndTradeType(trade);
            return ReturnResponse.backSuccess(dto);
        }
        return ReturnResponse.backFail(MessageSourceHolder.getMessage("message48"));
    }


    //获取版本号
    @ApiOperation(value = "获取版本号")
    @PostMapping("/appVersion")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型0:安卓 1:ios", required = true, defaultValue = "0", paramType = "query")
    })
    public ReturnResponse appVersion(@RequestParam(value = "type",defaultValue = "0") Integer type) {
        VersionModelDto version = versionService.getConfig();
        Map<String, Object> map = new HashMap<String, Object>();
        String appAddress = version.getAndroidAddress();
        String appVersion = version.getAndroidVersion();
        String updateInfo = version.getAndroidInfo();
        if (type == 1) {
            appAddress = version.getIosAddress();
            appVersion = version.getIosVersion();
            updateInfo = version.getIosInfo();
        }
        map.put("appAddress", appAddress);
        map.put("appVersion", appVersion);
        map.put("updateInfo", updateInfo);
        return ReturnResponse.backSuccess( map);
    }

    @ApiOperation(value = "获取下载地址")
    @PostMapping("getAppAddress")
    public ReturnResponse getAppAddress(HttpServletRequest request){
        VersionModelDto version = versionService.getConfig();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("android",version.getAndroidAddress());
        map.put("ios",version.getIosAddress());
        return ReturnResponse.backSuccess(map);
    }

    /**
     * 查询排行
     * @param index  0涨幅榜    1成交榜
     * @return
     */
    @ApiOperation(value = "查询排行")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="index" ,value = "排行0：涨幅    1：成交量",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="sort" ,value = "方向0升 1降",required = true,paramType = "query")
    })
    @PostMapping(value = "/indexRanking")
    public ReturnResponse indexRanking(@RequestParam(defaultValue = "0")Integer index,@RequestParam(defaultValue = "0")Integer sort) {
        //查询交易对
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        List<TradeInfoDto> tradeList=new ArrayList<TradeInfoDto>();
        if(areaList!=null && areaList.size()>0){
            for(CurrencyAreaModel area:areaList){
                tradeList.addAll(indexService.getByAreaId(area.getId()));
            }
        }
        if (tradeList.size() > 0) {
            Function<TradeInfoDto, BigDecimal> extractIdWay=null;
            if (index == 0) {
                extractIdWay  = TradeInfoDto::getRise;
            } else {
                extractIdWay  = TradeInfoDto::getTradeMoney;
            }
            Comparator<TradeInfoDto> comparator = Comparator.comparing(extractIdWay);
            //排序
            if(sort==0){
                tradeList=tradeList.stream().sorted(comparator.reversed()).collect(Collectors.toList());
            }else{
                tradeList=tradeList.stream().sorted(comparator).collect(Collectors.toList());
            }

        }
        return ReturnResponse.backSuccess(tradeList);
    }


    /**
     * 获取资讯
     * @return
     */
    @ApiOperation(value = "获取资讯")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping(value = "/info")
    public ReturnResponse info(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                               @RequestParam(defaultValue = "20", required = false) final Integer pageSize,HttpServletRequest request) {
        PageInfo inf=null;
        if(pageNum==1){//查询缓存
            String value = redisRepository.getString(KeyConstant.KEY_INFO_LIST);
            if(StringUtils.isNotEmpty(value)){
                inf=JSONObject.parseObject(value,PageInfo.class);
            }else {
                inf = informationService.queryFrontPageByDto(new InformationModelDto(), pageNum, pageSize);
                redisRepository.set(KeyConstant.KEY_INFO_LIST, JSONObject.toJSONString(inf), 5L, TimeUnit.MINUTES);
            }
        }else{
            inf=informationService.queryFrontPageByDto(new InformationModelDto(),pageNum,pageSize);
        }
        return ReturnResponse.backSuccess(inf);
    }



    /**
     * 获取币种列表
     */
    @ApiOperation(value = "获取所有币种列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "type", value = "查询类型 1所有币种 2可以充值币种 3可以提现币种", required = true, defaultValue = "1",paramType = "query")
    })
    @PostMapping("/currencyList")
    public ReturnResponse currencyList(@RequestParam(value = "type",defaultValue = "1")Integer type, HttpServletRequest request) {
        CurrencyConfigModelDto search=new CurrencyConfigModelDto();
        if(type==2) {
            search.setRechargeSwitch(SwitchEnum.ON.getCode());
        }else if(type==3){
            search.setExtractSwitch(SwitchEnum.ON.getCode());
        }
        //查询所有币种和配置信息
        List<CurrencyConfigModelDto> list = currencyConfigService.queryListByDto(search,true);
        List<SelectDto> curList=new ArrayList<>();
        if(list!=null && list.size()>0){
            //排序
            Collections.sort(list, new Comparator<CurrencyConfigModelDto>() {
                @Override
                public int compare(CurrencyConfigModelDto c1, CurrencyConfigModelDto c2) {
                    return c1.getPosition().compareTo(c2.getPosition());
                }
            });
            for (CurrencyConfigModelDto config:list){
                curList.add(new SelectDto(config.getCurrencyId(),config.getCurrencyName()));
            }
        }
        return ReturnResponse.backSuccess(curList);
    }
    /**
     * 获取图片验证吗
     *
     * @return
     */
    @ApiOperation(value = "获取图片验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "key", value = "传入验证key", required = true, paramType = "query")
    })
    @PostMapping("/getImage")
    public ReturnResponse getImage(@RequestParam(value = "key",required = false,defaultValue = "")String key,
                                   HttpServletRequest request, HttpServletResponse response) {
        System.out.println(StringUtils.isEmpty(key) +"-"+ Captcha.hashKey(key));
        if(StringUtils.isEmpty(key) || !Captcha.hashKey(key)){
            key=UUID.randomUUID().toString().replace("-","");
        }
        return ReturnResponse.backSuccess(Captcha.saveCaptcha(key));
    }
    /**
     * 测试验证码
     *
     * @return
     */
    @ApiOperation(value = "获取图片验证吗")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "key", value = "传入验证key", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "", required = true, paramType = "query")
    })
    @PostMapping("/checkImage")
    public ReturnResponse checkImage(@RequestParam(value = "key",required = false)String key,
                                     @RequestParam(value = "code",required = true)String code,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sessionId = request.getSession().getId();
        if(StringUtils.isEmpty(key)){
            key = sessionId;
        }
        if(StringUtils.isEmpty(key)){
            return ReturnResponse.backFail("key is null");
        }
        Captcha.checkCaptcha(key,code);
        return ReturnResponse.backSuccess("success");
    }
    /**
     * usdt人民币价格
     */
    @ApiOperation(value = "usdt人民币价格")
    @PostMapping("/usdtPrice")
    public ReturnResponse usdtPrice(@RequestParam(value = "type", defaultValue = "1") Integer type, HttpServletRequest request) {
        BigDecimal price = searchPrice.getUsdtQcPrice();
        return ReturnResponse.backSuccess(price);
    }

    /**
     * 精选
     * @return
     */
    @ApiOperation(value = "精选列表")
    @PostMapping("/important/list")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @ResponseBody
    public ReturnResponse getImportantInfoList(Integer pageNum, Integer pageSize) {
        PageInfo<CoinArticleModelDto> inf = null;
        if (pageNum == 1) {//查询缓存
            String value = redisRepository.getString(KeyConstant.KEY_INFO_LIST);
            if (StringUtils.isNotEmpty(value)) {
                inf = JSONObject.parseObject(value, PageInfo.class);
            } else {
                CoinArticleModelDto dto = new CoinArticleModelDto();
                inf = coinArticleService.queryFrontPageByDto(dto, pageNum, pageSize);
                redisRepository.set(KeyConstant.KEY_INFO_LIST, JSONObject.toJSONString(inf), 5L, TimeUnit.MINUTES);
            }
        } else {
            CoinArticleModelDto dto = new CoinArticleModelDto();
            inf = coinArticleService.queryFrontPageByDto(dto, pageNum, pageSize);
        }
        return ReturnResponse.backSuccess(inf);
    }

    @ApiOperation(value = "精选详情")
    @PostMapping("/important/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "id", value = "精选id", required = false, paramType = "query", defaultValue = "1")
    })
    public ReturnResponse getDetail(HttpServletRequest request, @RequestParam(value = "id") Long id) {
        CoinArticleModelDto model = coinArticleService.getById(id);
        return ReturnResponse.backSuccess(MessageSourceHolder.getMessage("ctcTradeNote2"), model);
    }

    @ApiOperation(value = "AFIL价格")
    @PostMapping("getAFILPrice")
    public ReturnResponse getAFILPrice(HttpServletRequest request){
        return ReturnResponse.backSuccess(configService.queryValueByName(ConfigConstant.CONFIG_PRICE_AFIL));
    }

    @ApiOperation(value = "激活账号AFIL费用")
    @PostMapping("activeAFILPrice")
    public ReturnResponse activeAFILPrice(HttpServletRequest request){
        return ReturnResponse.backSuccess(addressRecordService.getTrustFee());
    }
}
