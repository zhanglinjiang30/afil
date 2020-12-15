package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ComputePowerRecordModelDto extends ComputePowerRecordModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String currencyName;

    private String address;

    private BigDecimal holding;

}
