package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.LockReleaseModelDto;
import com.liuqi.business.model.LockTransferInputModel;
import com.liuqi.business.model.LockTransferInputModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.LockTransferInputService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/lockTransferInput")
public class AdminLockTransferInputController extends BaseAdminController<LockTransferInputModel,LockTransferInputModelDto> {

    @Autowired
    private LockTransferInputService lockTransferInputService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="lockTransferInput";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.lockTransferInputService;
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
    private final static String DEFAULT_EXPORTNAME="转入";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","用户id","申请数量","到账数量","杠杆倍数"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyName","userName","applyQuantity","quantity","lever"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyService currencyService;

    @Override
    protected void listHandle(LockTransferInputModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName = dto.getUserName();
        if (StringUtils.isNotEmpty(userName)) {
//            Long userId = userService.queryIdByName(userName);
//            dto.setUserId(userId);
        }
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);
    }


}
