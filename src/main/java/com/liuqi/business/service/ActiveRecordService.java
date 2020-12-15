package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.ActiveRecordModel;
import com.liuqi.business.model.ActiveRecordModelDto;
import com.liuqi.response.ReturnResponse;

import java.util.List;

public interface ActiveRecordService extends BaseService<ActiveRecordModel,ActiveRecordModelDto>{

    /**
     * @Description 添加激活记录
     * @Date 15:34 2020/8/14
     */
    void addActiveRecord(Long fromAddressId, Long toAddressId, String activeDevice);

    /**
     * @Description 获取当前显示地址的邀请记录
     * @Date 15:33 2020/8/14
     */
    ReturnResponse getActiveList(LoginDto loginDto, Long currencyId, Integer pageNum, Integer pageSize);

    /**
     * @Description 修改被激活地址显示的名称
     * @Date 15:33 2020/8/14
     */
    ReturnResponse modifyName(Long id, String name);

    ReturnResponse refreshActiveData();

    Long getParentByToAddressId(Long toAddressId);

    List<Long> getToIdsByFromAddressId(Long fromAddressId);
}
