package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.AirdropRecordModel;
import com.liuqi.business.model.AirdropRecordModelDto;
import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;

public interface AirdropRecordService extends BaseService<AirdropRecordModel,AirdropRecordModelDto>{

    /**
     * @Description 空投
     * @Date 18:06 2020/8/12
     */
    ReturnResponse doAirDrop(LoginDto loginDto, String tradePwd, BigDecimal sucAmount);

}
