package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.UserWalletLogModel;
import com.liuqi.business.model.UserWalletLogModelDto;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UserWalletLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/userWalletLog")
public class AdminUserWalletLogController extends BaseAdminController<UserWalletLogModel, UserWalletLogModelDto> {

    @Autowired
    private UserWalletLogService userWalletLogService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "userWalletLog";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";

    @Override
    public BaseService getBaseService() {
        return this.userWalletLogService;
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
    private final static String DEFAULT_EXPORTNAME = "钱包日志";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"用户", "币种", "金额", "类型", "创建时间", "备注", "余额", "快照"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"userName", "currencyName", "money", "typeStr", "createTime", "remark", "balance", "snapshot"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AddressRecordService addressRecordService;

    @Override
    protected void listHandle(UserWalletLogModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName = dto.getUserName();
        if (StringUtils.isNotEmpty(userName)) {
            Long userId = addressRecordService.getIdByAddress(userName);
            dto.setUserId(userId);
        }
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("currencyList", list);

        List<SelectDto> typeList = WalletLogTypeEnum.getList();
        modelMap.put("typeList", typeList);
    }
}
