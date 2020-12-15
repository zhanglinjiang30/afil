package com.liuqi.business.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
public class MiningTotalProfitDto implements Serializable{
    //时间
    @JsonFormat(pattern = "MM-dd",timezone="GMT+8")
    private Date date;
    //价格
    private BigDecimal totalProfit;
}
