package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.SDto;
import com.liuqi.business.model.ActiveRecordModel;
import com.liuqi.business.model.ActiveRecordModelDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiveRecordMapper extends BaseMapper<ActiveRecordModel,ActiveRecordModelDto>{

    /**
     * @Description 查询某个助记词激活某台设备的次数
     * @Date 11:37 2020/8/13
     */
    Integer getPActiveDeviceCount(Long fromPId, String activeDevice);

    /**
     * @Description 查询某个地址激活某台设备的次数
     * @Date 11:37 2020/8/13
     */
    Integer getAActiveDeviceCount(Long fromAddressId, String activeDevice);

    SDto getActiveData(List<Long> list);

    Long getParentByToAddressId(@Param("toAddressId") Long toAddressId);

    List<Long> getToIdsByFromAddressId(@Param("fromAddressId") Long fromAddressId);
}
