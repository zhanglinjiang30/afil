package com.liuqi.business.controller.sys;


import com.liuqi.base.BaseService;
import com.liuqi.base.BaseSysController;
import com.liuqi.base.LoginSysUserHelper;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.ExtractMoneyEnum;
import com.liuqi.business.model.AddressRecordModelDto;
import com.liuqi.business.model.RechargeModel;
import com.liuqi.business.model.RechargeModelDto;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.RechargeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/recharge")
public class SysRechargeController extends BaseSysController<RechargeModel, RechargeModelDto> {

    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private AddressRecordService addressRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "sys/recharge";


    @Override
    protected BaseService getBaseService() {
        return this.rechargeService;
    }

    @Override
    protected String getJspBasePath() {
        return JSP_BASE_PTH;
    }


    /*******自己代码**********************************************************************************************************/
    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<SelectDto> statusList = ExtractMoneyEnum.getList();
        modelMap.put("statusList", statusList);
    }

    @Override
    protected void listHandle(RechargeModelDto dto, HttpServletRequest request) {
        super.listHandle(dto, request);
        Long currencyId = LoginSysUserHelper.getUserCurrencyId();
        dto.setCurrencyId(currencyId);
        //设置用户
        if(StringUtils.isNotEmpty(dto.getSucAddress())){
            AddressRecordModelDto a = addressRecordService.getByAddress(dto.getSucAddress());
            dto.setUserId(a == null ? 0l : a.getId());
        }
    }
}
