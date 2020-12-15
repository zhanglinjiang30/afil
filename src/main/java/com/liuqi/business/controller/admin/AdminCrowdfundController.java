package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.CrowdfundStatusEnum;
import com.liuqi.business.enums.CrowdfundTypeEnum;
import com.liuqi.business.model.CrowdfundModel;
import com.liuqi.business.model.CrowdfundModelDto;
import com.liuqi.business.service.CrowdfundService;
import com.liuqi.business.service.CurrencyService;
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
@RequestMapping("/admin/crowdfund")
public class AdminCrowdfundController extends BaseAdminController<CrowdfundModel,CrowdfundModelDto> {

    @Autowired
    private CrowdfundService crowdfundService;

    @Autowired
    private CurrencyService currencyService;

    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="crowdfund";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.crowdfundService;
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
        String[] headers = {"id","创建时间","更新时间","版本号","备注","名称","第几次","开始时间","结束时间","状态 0 禁用 1启用"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","version","remark","name","index","startTime","endTime","status"};
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
    protected void toUpdateHandle(CrowdfundModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(CrowdfundModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        modelMap.put("currencyList",currencyService.getAll());
        modelMap.put("statusList", CrowdfundStatusEnum.getList());
        modelMap.put("typeList", CrowdfundTypeEnum.getList());
    }

    @RequestMapping("toSubInfoList")
    public String toSubInfo(HttpServletRequest request,HttpServletResponse response,
                            ModelMap modelMap,Long crowdfundId){
        modelMap.put("crowdfundId",crowdfundId);
        this.toAddHandle(modelMap,request,response);
        return getJspBasePath()+"/crowdfundInfo/crowdfundInfoList";
    }
}
