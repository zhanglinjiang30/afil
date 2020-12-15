package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.model.AddressRecordModelDto;
import com.liuqi.business.model.UserRechargeAddressModel;
import com.liuqi.business.model.UserRechargeAddressModelDto;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.UserRechargeAddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/userRechargeAddress")
public class AdminUserReChargeAddressController extends BaseAdminController<UserRechargeAddressModel, UserRechargeAddressModelDto> {

    @Autowired
    private UserRechargeAddressService userRechargeAddressService;
    @Autowired
    private AddressRecordService addressRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "userRechargeAddress";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";

    @Override
    public BaseService getBaseService() {
        return this.userRechargeAddressService;
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
    public String getNotOperate() {
        return NOT_OPERATE;
    }

    @Override
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }

    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME = "用户绑定地址";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"用户", "充币地址", "充币路径", "协议"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"userName", "address", "path", "protocolStr"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/

    @Override
    protected void listHandle(UserRechargeAddressModelDto dto, HttpServletRequest request) {
        super.listHandle(dto, request);
        //设置用户
        if (StringUtils.isNotEmpty(dto.getSucAddress())) {
            AddressRecordModelDto a = addressRecordService.getById(dto.getUserId(), false);
            dto.setUserId(a == null ? 0L : a.getId());
        }
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<SelectDto> protocolList = ProtocolEnum.getList();
        modelMap.put("protocolList", protocolList);
    }
}
