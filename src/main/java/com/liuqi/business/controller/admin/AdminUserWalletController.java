package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/userWallet")
public class AdminUserWalletController extends BaseAdminController<UserWalletModel,UserWalletModelDto> {

    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private AddressRecordService addressRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="userWallet";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";
    @Override
    public BaseService getBaseService() {
        return this.userWalletService;
    }

    @Override
    public String getJspBasePath() {
        return JSP_BASE_PTH;
    }
    @Override
    public String getBaseModuel() {
        return BASE_MODUEL;
    }
    @Override
    public String getNotOperate() { return NOT_OPERATE;}
    @Override
    public String getDefaultExportName() { return DEFAULT_EXPORTNAME;}
    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="用户钱包";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"用户","币种","可用数量","冻结数量","创建时间"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"userName","currencyName","using","freeze","createTime"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
    @Override
    protected void listHandle(UserWalletModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        if(StringUtils.isNotEmpty(dto.getSucAddress())){
            AddressRecordModelDto a = addressRecordService.getByAddress(dto.getSucAddress());
            dto.setUserId(a == null ? 0L : a.getId());
        }
    }
    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list=currencyService.getAll();
        modelMap.put("currencyList",list);
    }

    @Override
    protected void toUpdateHandle(UserWalletModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String userName = addressRecordService.getById(t.getUserId(), false).getAddress();
        String currencyName= currencyService.getById(t.getCurrencyId()).getName();
        modelMap.put("userName",userName);
        modelMap.put("currencyName",currencyName);
    }
    /**
     * （后台）修改
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/updateP")
    @ResponseBody
    public ReturnResponse updateP(UserWalletModelDto model, HttpServletRequest request) {
        Long userId = LoginAdminUserHelper.getAdminId();
        if(model.getFreeze().compareTo(BigDecimal.ZERO)<0){
            return ReturnResponse.backFail("冻结币不能输入小于0的数");
        }
        if(!checkPwd(userId,model.getOpePwd())){
            return ReturnResponse.backFail("密码错误");
        }
        userWalletService.adminUpdate(model,userId);
        return ReturnResponse.backSuccess();
    }

    @RequestMapping("/update/air/drop/amount")
    @ResponseBody
    public ReturnResponse updateAirDropAmount(BigDecimal minAmount, BigDecimal maxAmount, BigDecimal amount, HttpServletRequest request) {
        UserWalletModelDto params = new UserWalletModelDto();
        params.setMinAmount(minAmount);
        params.setMaxAmount(maxAmount);
        params.setCurrencyId(currencyService.getByName("USDT").getId());
        List<UserWalletModelDto> list = userWalletService.queryListByDto(params, false);
        if (list == null || list.isEmpty()) {
            return ReturnResponse.backFail("没有满足条件地址");
        }
        List<Long> l = list.stream().map(UserWalletModelDto::getUserId).collect(Collectors.toList());
        addressRecordService.updateAirDropAmount(l, amount);
        return ReturnResponse.backSuccess();
    }

    /**
     * 补全钱包
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/toInitWallet")
    public String toInitWallet(HttpServletRequest request) {
        return getJspBasePath() + "/" + getBaseModuel() + "/initWallet";
    }

    @RequestMapping("/toSetAirDropAmount")
    public String toAirDropAmount(HttpServletRequest request, ModelMap modelMap, BigDecimal minAmount, BigDecimal maxAmount) {
        System.out.println("==========" + minAmount + "---" + maxAmount);
        modelMap.put("minAmount", minAmount);
        modelMap.put("maxAmount", maxAmount);
        return getJspBasePath() + "/" + getBaseModuel() + "/airDropAmount";
    }

    /**
     * （初始化
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/initWallet")
    @ResponseBody
    public ReturnResponse initWallet(@RequestParam("userName") String userName, HttpServletRequest request) {
        AddressRecordModelDto dto = addressRecordService.getByAddress(userName);
        if (dto == null) {
            return ReturnResponse.backFail("地址不存在");
        }
        userWalletService.insertUserWallet(dto.getId());
        return ReturnResponse.backSuccess();
    }
}
