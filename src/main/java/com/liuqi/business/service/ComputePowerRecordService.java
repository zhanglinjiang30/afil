package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.ComputePowerRecordModel;
import com.liuqi.business.model.ComputePowerRecordModelDto;
import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;

public interface ComputePowerRecordService extends BaseService<ComputePowerRecordModel,ComputePowerRecordModelDto>{

    void add(Long userId, Long pUserId, Long currencyId, BigDecimal power, Integer largeZone);

    ReturnResponse getInviteList(LoginDto loginDto, Long currencyId, Integer pageNum, Integer pageSize);
}
