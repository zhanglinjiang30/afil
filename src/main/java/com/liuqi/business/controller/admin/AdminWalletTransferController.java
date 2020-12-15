package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.WalletTransferModel;
import com.liuqi.business.model.WalletTransferModelDto;
import com.liuqi.business.service.WalletTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import com.liuqi.business.dto.SelectDto;

@Controller
@RequestMapping("/admin/walletTransfer")
public class AdminWalletTransferController extends BaseAdminController<WalletTransferModel,WalletTransferModelDto> {

    @Autowired
    private WalletTransferService walletTransferService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="walletTransfer";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.walletTransferService;
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
    private final static String DEFAULT_EXPORTNAME="xxx";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","用户id","币种id","数量","来源钱包 1资产 2 法币 3 矿池","目标钱包 1资产 2 法币 3 矿池","状态 1未处理 2成功 3拒绝 4失败"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","userId","currencyId","quantity","source","target","status"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/



    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toAddHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(WalletTransferModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(WalletTransferModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
    }

}
