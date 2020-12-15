package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.PublicOfferStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class PoolProfitConfigModelDto extends PoolProfitConfigModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";
}
