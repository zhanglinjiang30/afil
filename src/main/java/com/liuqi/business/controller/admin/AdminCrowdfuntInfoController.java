package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CrowdfundInfoModel;
import com.liuqi.business.model.CrowdfundInfoModelDto;
import com.liuqi.business.service.CrowdfundInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/crowdfundInfo")
public class AdminCrowdfuntInfoController extends BaseAdminController<CrowdfundInfoModel, CrowdfundInfoModelDto> {

    @Autowired
    private CrowdfundInfoService crowdfundInfoService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="crowdfundInfo";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.crowdfundInfoService;
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
        String[] headers = {"id","创建时间","更新时间","版本号","备注","投票id","总额度","单笔最低额度","单笔最高额度","已用额度","返回本金比例","静态奖励比例","直推奖励比例","状态 1 进行中 2 已结束"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","version","remark","crowdfundId","totalQuota","minQuota","maxQuota","usedQuota","refundRate","staticRewardRate","directRewardRate","status"};
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
    protected void toUpdateHandle(CrowdfundInfoModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(CrowdfundInfoModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
    }

    @RequestMapping("toAddByCrowdfundId")
    public String toAddByCrowdfundId(ModelMap modelMap,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     Long crowdfundId) {
        modelMap.put("crowdfundId",crowdfundId);
        return super.toAdd(modelMap, request, response);
    }
}
