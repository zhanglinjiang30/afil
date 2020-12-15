package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.PoolProfitRecordModel;
import com.liuqi.business.model.PoolProfitRecordModelDto;
import com.liuqi.business.service.PoolProfitRecordService;
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
@RequestMapping("/admin/poolProfitRecord")
public class AdminPoolProfitRecordController extends BaseAdminController<PoolProfitRecordModel,PoolProfitRecordModelDto> {

    @Autowired
    private PoolProfitRecordService poolProfitRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="poolProfitRecord";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.poolProfitRecordService;
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
    private final static String DEFAULT_EXPORTNAME="poolProfitRecord";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","币种id","用户id","统计日期","挖矿收益【静态收益】","推广收益【动态收益】","持币收益率"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","currencyId","userId","statisticDate","staticProfit","dynamicProfit","profitRate"};
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
    protected void toUpdateHandle(PoolProfitRecordModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(PoolProfitRecordModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
    }

}
