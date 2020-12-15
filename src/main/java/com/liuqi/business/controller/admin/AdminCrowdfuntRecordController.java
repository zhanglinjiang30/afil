package com.liuqi.business.controller.admin;


import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.CrowdfundRecordModel;
import com.liuqi.business.model.CrowdfundRecordModelDto;
import com.liuqi.business.service.CrowdfundRecordService;
import com.liuqi.response.DataResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/crowdfundRecord")
public class AdminCrowdfuntRecordController extends BaseAdminController<CrowdfundRecordModel, CrowdfundRecordModelDto> {

    @Autowired
    private CrowdfundRecordService crowdfundRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="crowdfundRecord";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.crowdfundRecordService;
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
        String[] headers = {"id","创建时间","更新时间","版本号","备注","地址id","投票详情id","币种id","数量"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","version","remark","addressId","crowdfundInfoId","currencyId","quantity"};
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
    protected void toUpdateHandle(CrowdfundRecordModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(CrowdfundRecordModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
    }

    @Override
    public DataResult getList(CrowdfundRecordModelDto crowdfundRecordModelDto, HttpServletRequest request, int page, int limit) {
        PageInfo<CrowdfundRecordModelDto> pageInfo = crowdfundRecordService.getRecordList(page,limit,crowdfundRecordModelDto);
        return new DataResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
