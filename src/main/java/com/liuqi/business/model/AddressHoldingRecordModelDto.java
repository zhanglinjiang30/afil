package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AddressHoldingRecordModelDto extends AddressHoldingRecordModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String privateKey;// 地址的私钥

    private String address;// 地址

    private BigDecimal sucAmount;// 地址对应的

}
