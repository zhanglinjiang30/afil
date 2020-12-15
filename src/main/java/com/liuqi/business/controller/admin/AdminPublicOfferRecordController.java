package com.liuqi.business.controller.admin;


import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.PublicOfferRecordModel;
import com.liuqi.business.model.PublicOfferRecordModelDto;
import com.liuqi.business.service.PublicOfferRecordService;
import com.liuqi.response.DataResult;
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
@RequestMapping("/admin/publicOfferRecord")
public class AdminPublicOfferRecordController extends BaseAdminController<PublicOfferRecordModel,PublicOfferRecordModelDto> {

    @Autowired
    private PublicOfferRecordService publicOfferRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="publicOfferRecord";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.publicOfferRecordService;
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
    private final static String DEFAULT_EXPORTNAME="asszz";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"id","开始时间","更新时间","版本号","备注","用户地址ID","公募id","数量"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","version","remark","addressId","publicOfferId","quantity"};
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
    protected void toUpdateHandle(PublicOfferRecordModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(PublicOfferRecordModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
    }

    @Override
    public DataResult getList(PublicOfferRecordModelDto publicOfferRecordModelDto, HttpServletRequest request, int page, int limit) {
        PageInfo<PublicOfferRecordModelDto> pageInfo = publicOfferRecordService.getRecordList(page,limit,publicOfferRecordModelDto);
        return new DataResult(pageInfo.getTotal(),pageInfo.getList());
    }
}
