package com.liuqi.business.controller.admin;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseController;
import com.liuqi.business.dto.WalletDto;
import com.liuqi.business.dto.stat.*;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.CtcOrderStatusEnum;
import com.liuqi.business.mapper.StatMapper;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.UserAuthService;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/stat")
public class AdminStatController extends BaseController {

    @Autowired
    private StatMapper statMapper;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserAuthService userAuthService;
    /*********************************************************************************************/

    /**
     * 用户新增统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toUser")
    public String toUser(ModelMap modelMap) {
        return "admin/stat/user";
    }

    @RequestMapping(value = "/userStat")
    @ResponseBody
    public ReturnResponse userStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                   @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<UserStatDto> list = statMapper.userStat(startDate,endDate);
        return ReturnResponse.backSuccess(list);
    }
    /*********************************************************************************************/
    /**
     * 充值统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toRecharge")
    public String toRecharge(ModelMap modelMap) {
        return "admin/stat/recharge";
    }

    @RequestMapping(value = "/rechargeStat")
    @ResponseBody
    public ReturnResponse rechargeStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                       @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        //7天前的时间
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<RechargeExtractStatDto> list = statMapper.rechargeStat(startDate,endDate);
        list.stream().forEach(dto->{
            CurrencyModel model=currencyService.getById(dto.getCurrencyId());
            dto.setCurrencyName(model!=null?model.getName():"");
        });
        return getJsonData(startDate,endDate,list);
    }
    /*********************************************************************************************/
    /**
     * 提现统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toExtract")
    public String toExtract(ModelMap modelMap) {
        return "admin/stat/extract";
    }

    @RequestMapping(value = "/extractStat")
    @ResponseBody
    public ReturnResponse extractStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                      @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        //7天前的时间
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<RechargeExtractStatDto> list = statMapper.extractStat(startDate,endDate);
        list.stream().forEach(dto->{
            CurrencyModel model=currencyService.getById(dto.getCurrencyId());
            dto.setCurrencyName(model!=null?model.getName():"");
        });
        return getJsonData(startDate,endDate,list);
    }

    private ReturnResponse getJsonData(Date startDate ,Date endDate ,List<RechargeExtractStatDto> list){
        String dateFormat="yyyy-MM-dd";

        List<String> dateList=new ArrayList<String>();
        List<CurrencyModelDto> currencyList = currencyService.getAll();
        Map<String,List<BigDecimal>> dataMap=new HashMap<String,List<BigDecimal>>();
        Long count=DateUtil.between(startDate,endDate, DateUnit.DAY);
        if(list!=null && list.size()>0){
            Map<String,BigDecimal> dataTempMap= list.stream().collect(Collectors.toMap(RechargeExtractStatDto->DateTime.of(RechargeExtractStatDto.getDate()).toString(dateFormat)+"_"+RechargeExtractStatDto.getCurrencyId(),RechargeExtractStatDto->RechargeExtractStatDto.getTotal()));

            String dateStr="";
            //组装数据
            for(int i=0;i<count;i++){
                dateStr=DateTime.of(startDate).offset(DateField.DAY_OF_YEAR,1).toString(dateFormat);
                //日期
                dateList.add(dateStr);
                String tempKey="";
                for(CurrencyModelDto dto:currencyList){
                    //判断币种名称存不存在  不存在插入一条新数据
                    if(!dataMap.containsKey(dto.getName())){
                        dataMap.put(dto.getName(),new ArrayList<BigDecimal>());
                    }
                    tempKey=dateStr+"_"+dto.getId();
                    //存在key
                    if(dataTempMap.containsKey(tempKey)){
                        dataMap.get(dto.getName()).add(dataTempMap.get(tempKey));
                    }else{
                        dataMap.get(dto.getName()).add(BigDecimal.ZERO);
                    }
                }
            }
        }
        JSONObject obj=new JSONObject();
        obj.put("date",dateList);
        obj.put("data",dataMap);
        obj.put("list",list);
        return ReturnResponse.backSuccess(obj);
    }

    /*********************************************************************************************/
    /**
     * 交易统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toTrade")
    public String toTrade(ModelMap modelMap) {
        return "admin/stat/trade";
    }

    @RequestMapping(value = "/tradeStat")
    @ResponseBody
    public ReturnResponse tradeStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                    @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        String dateFormat="yyyy-MM-dd";
        //7天前的时间
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<TradeStatDto> list = statMapper.tradeStat(startDate,endDate);
        list.stream().forEach(dto->{
            CurrencyTradeModelDto model=currencyTradeService.getById(dto.getTradeId());
            dto.setAreaName(model!=null?model.getAreaName():"");
            dto.setTradeName(model!=null?model.getTradeCurrencyName()+"/"+model.getCurrencyName():"");
        });

        List<String> dateList=new ArrayList<String>();
        List<CurrencyTradeModelDto> tradeList= currencyTradeService.queryListByDto(new CurrencyTradeModelDto(),true);
        //交易币/币种(区域)
        Map<String,List<BigDecimal>> dataMap=new HashMap<String,List<BigDecimal>>();
        Long count=DateUtil.between(startDate,endDate, DateUnit.DAY);
        if(list!=null && list.size()>0){
            Map<String,BigDecimal> dataTempMap= list.stream().collect(Collectors.toMap(TradeStatDto->DateTime.of(TradeStatDto.getDate()).toString(dateFormat)+"_"+TradeStatDto.getTradeId(),TradeStatDto->TradeStatDto.getQuantity()));

            String dateStr="";
            //组装数据
            for(int i=0;i<count;i++){
                dateStr=DateTime.of(startDate).offset(DateField.DAY_OF_YEAR,1).toString(dateFormat);
                //日期
                dateList.add(dateStr);
                String tempKey="";
                String name="";
                for(CurrencyTradeModelDto dto:tradeList){
                    name=dto.getTradeCurrencyName()+"/"+dto.getCurrencyName()+"("+dto.getAreaName()+")";
                    //判断币种名称存不存在  不存在插入一条新数据
                    if(!dataMap.containsKey(name)){
                        dataMap.put(name,new ArrayList<BigDecimal>());
                    }
                    tempKey=dateStr+"_"+dto.getId();
                    //存在key
                    if(dataTempMap.containsKey(tempKey)){
                        dataMap.get(name).add(dataTempMap.get(tempKey));
                    }else{
                        dataMap.get(name).add(BigDecimal.ZERO);
                    }
                }
            }
        }
        JSONObject obj=new JSONObject();
        obj.put("date",dateList);
        obj.put("data",dataMap);
        obj.put("list",list);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * ctc商户统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toCtc")
    public String toCtc(ModelMap modelMap) {
        return "admin/stat/ctc";
    }

    @RequestMapping(value = "/ctcStat")
    @ResponseBody
    public ReturnResponse ctcStat(@RequestParam(value = "startDate", required = false) Date startDate,
                                  @RequestParam(value = "endDate", required = false) Date endDate) throws Exception {
        Map<String, CtcStatShowDto> map = new HashMap<>();
        List<CtcStatDto> list = statMapper.ctcStat(startDate, endDate);
        if (list != null) {
            String key = "";
            for (CtcStatDto ctc : list) {
                key = ctc.getStoreId() + "_" + ctc.getCurrencyId() + "_" + ctc.getTradeType();
                CtcStatShowDto dto = map.get(key);
                if (dto == null) {
                    dto = new CtcStatShowDto();
//                    dto.setStoreName(userService.getNameById(ctc.getStoreId()));
                    dto.setRealName(userAuthService.getNameByUserId(ctc.getStoreId()));
                    dto.setCurrencyName(currencyService.getNameById(ctc.getCurrencyId()));
                    //买的 对应承运商收入  卖的对应支出
                    dto.setTradeTypeStr(BuySellEnum.BUY.getCode().equals(ctc.getTradeType()) ? "收入" : "支付");
                    map.put(key, dto);
                }
                if (CtcOrderStatusEnum.WAIT.getCode().equals(ctc.getStatus())) {
                    dto.setWaitMoney(ctc.getMoney());
                } else if (CtcOrderStatusEnum.RUNING.getCode().equals(ctc.getStatus())) {
                    dto.setRuningMoney(ctc.getMoney());
                } else if (CtcOrderStatusEnum.END.getCode().equals(ctc.getStatus())) {
                    dto.setEndMoney(ctc.getMoney());
                } else if (CtcOrderStatusEnum.CANCEL.getCode().equals(ctc.getStatus())) {
                    dto.setCancelMoney(ctc.getMoney());
                }
            }
        }
        return ReturnResponse.backSuccess(map.values());
    }


    /**
     * 钱包总统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toWallet")
    public String toWallet(ModelMap modelMap) {
        return "admin/stat/wallet";
    }

    @RequestMapping(value = "/walletStat")
    @ResponseBody
    public ReturnResponse walletStat() throws Exception {
        Map<Long, WalletStatDto> map = new HashMap<>();
        List<CurrencyModelDto> cList = currencyService.getAll();
        List<WalletDto> using = statMapper.getUsing();
        Map<Long, WalletDto> usingMap = using.stream().collect(Collectors.toMap(t -> t.getCurrencyId(), t -> t));
        List<WalletDto> locking = statMapper.getLock();
        Map<Long, WalletDto> lockingMap = locking.stream().collect(Collectors.toMap(t -> t.getCurrencyId(), t -> t));
        Long currencyId = 0L;
        WalletDto temWallet = null;
        for (CurrencyModelDto currency : cList) {
            currencyId = currency.getId();
            WalletStatDto temp = map.get(currencyId);
            if (temp == null) {
                temp = new WalletStatDto();
                temp.setCurrencyId(currencyId);
                temp.setCurrencyName(currency.getName());
                map.put(currencyId, temp);
            }
            //可用
            temWallet = usingMap.get(currencyId);
            if (temWallet != null) {
                temp.setUsing(temWallet.getUsing());
                temp.setFreeze(temWallet.getFreeze());
            }
            //锁仓
            temWallet = lockingMap.get(currencyId);
            if (temWallet != null) {
                temp.setLocking(temWallet.getUsing());
                temp.setLockFreeze(temWallet.getFreeze());
            }

        }

        return ReturnResponse.backSuccess(map.values());
    }
}
