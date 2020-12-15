package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tanyan
 * @create 2020-03=04
 * @description
 */
@Data
public class BaseWalletDto implements Serializable {

    private Long id;
    private String currencyName;
    private String totalCurrencyName;
    private BigDecimal using;
    private BigDecimal freeze;
    private BigDecimal guarantee;
    private BigDecimal usdtPrice;
    private BigDecimal price;
    private BigDecimal totalUsdt;
    private String pic;

    private BigDecimal airDropQuantity;
}
