package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ActiveRecordModelDto extends ActiveRecordModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String fromDeviceId;// 激活方首次创建的设备

    private String fromAddress;// 激活方地址

    private String toAddress;// 被激活地址

    private BigDecimal balance;// 余额

    private String currencyName;// 币种名称

    private Integer activeDeviceCount;// 激活设备数

    private Integer activeCount;// 激活数量
}
