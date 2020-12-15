package com.liuqi.business.controller.sys;


import com.liuqi.base.BaseService;
import com.liuqi.base.BaseSysController;
import com.liuqi.base.LoginSysUserHelper;
import com.liuqi.business.model.TradeRecordModel;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.TradeRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/sys/tradeRecord")
public class SysTradeRecordController extends BaseSysController<TradeRecordModel, TradeRecordModelDto> {

    @Autowired
    private TradeRecordService tradeRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "sys/tradeRecord";

    @Override
    protected BaseService getBaseService() {
        return this.tradeRecordService;
    }

    @Override
    protected String getJspBasePath() {
        return JSP_BASE_PTH;
    }


    /*******自己代码**********************************************************************************************************/
    @Autowired
    private AddressRecordService addressRecordService;

    @Override
    protected void listHandle(TradeRecordModelDto dto, HttpServletRequest request) {
        super.listHandle(dto, request);
        //查询自己交易对数据
        List<Long> tradeList = LoginSysUserHelper.getUserTradeId();
        dto.setSearchTradeList(true);
        dto.setTradeList(tradeList);
        //设置用户

        //设置用户
        String buyUserName = dto.getBuyUserName();
        if (StringUtils.isNotEmpty(buyUserName)) {
            Long userId = addressRecordService.getIdByAddress(buyUserName);
            dto.setBuyUserId(userId);
        }

        String sellUserName = dto.getSellUserName();
        if (StringUtils.isNotEmpty(sellUserName)) {
            Long userId = addressRecordService.getIdByAddress(sellUserName);
            dto.setSellUserId(userId);
        }
    }

}
