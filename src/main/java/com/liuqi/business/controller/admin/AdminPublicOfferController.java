package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.PublicOfferStatusEnum;
import com.liuqi.business.model.PublicOfferModel;
import com.liuqi.business.model.PublicOfferModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.PublicOfferService;
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
@RequestMapping("/admin/publicOffer")
public class AdminPublicOfferController extends BaseAdminController<PublicOfferModel,PublicOfferModelDto> {

    @Autowired
    private PublicOfferService publicOfferService;

    @Autowired
    private CurrencyService currencyService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="publicOffer";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.publicOfferService;
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
    private final static String DEFAULT_EXPORTNAME="xxxx";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"id","创建时间","更新时间","版本","备注","名称","期数","币种id","数量","已用额度","折扣比例","开始时间","结束时间","状态 0 未开始 1 已开始 2 已结束"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","version","remark","name","index","currencyId","quota","useQuota","discountRate","startTime","overTime","status"};
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
    protected void toUpdateHandle(PublicOfferModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(PublicOfferModelDto t,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t,modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap){
        modelMap.put("statusList", PublicOfferStatusEnum.getList());
        modelMap.put("currencyList",currencyService.getAll());
    }

}
