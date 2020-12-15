package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CrowdfundRewardConfigModel;
import com.liuqi.business.model.CrowdfundRewardConfigModelDto;
import com.liuqi.business.service.CrowdfundRewardConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/crowdfundRewardConfig")
public class AdminCrowdfuntRewardConfigController extends BaseAdminController<CrowdfundRewardConfigModel, CrowdfundRewardConfigModelDto> {

    @Autowired
    private CrowdfundRewardConfigService crowdfundRewardConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="crowdfundRewardConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.crowdfundRewardConfigService;
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
        String[] headers = {"id","创建时间","更新时间","版本","备注","等级","直推人数","小区总持币数量","奖励比例","最低下级数量","最高下级数量","状态 0 禁用，1 启用"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","version","remark","level","directCount","totalAmount","rewardRate","minSubCount","maxSubCount","status"};
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
    protected void toUpdateHandle(CrowdfundRewardConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(CrowdfundRewardConfigModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
    }

}
