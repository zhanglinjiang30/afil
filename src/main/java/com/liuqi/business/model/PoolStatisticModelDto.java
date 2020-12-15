package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PoolStatisticModelDto extends PoolStatisticModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String currencyName;// 币种名称

    private String currencyImage;// 币种图标

    private BigDecimal price;// 币种均价

    private BigDecimal minHolding;// 最低持币

    private BigDecimal niceHolding;// 最佳持币
}
