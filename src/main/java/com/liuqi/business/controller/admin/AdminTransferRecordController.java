package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.ConfirmStatusEnum;
import com.liuqi.business.model.TransferRecordModel;
import com.liuqi.business.model.TransferRecordModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.TransferRecordService;
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
@RequestMapping("/admin/transferRecord")
public class AdminTransferRecordController extends BaseAdminController<TransferRecordModel,TransferRecordModelDto> {

    @Autowired
    private TransferRecordService transferRecordService;
    @Autowired
    private CurrencyService currencyService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="transferRecord";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.transferRecordService;
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
    private final static String DEFAULT_EXPORTNAME="transferRecord";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","转出地址","转入地址","交易币种","交易数量","手续费币种","手续费数量","状态 0=未确认，1=已确认","交易哈希"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","fromAddress","toAddress","currencyId","amount","feeCurrencyId","fee","status","txHash"};
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
    protected void toUpdateHandle(TransferRecordModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(TransferRecordModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        modelMap.put("currencyIdList", currencyService.getAll());
        modelMap.put("statusList", ConfirmStatusEnum.values());
    }

}
