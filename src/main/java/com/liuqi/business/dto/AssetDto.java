package com.liuqi.business.dto;

import com.liuqi.business.enums.WalletTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author tanyan
 * @create 2020-03=04
 * @description
 */
@Data
public class AssetDto<T> implements Serializable {
    private Integer type;
    private String name;
    private List<T> list;
    private BigDecimal usdtTotal;
    private BigDecimal total;

    public AssetDto() {
    }

    public AssetDto(WalletTypeEnum asset, List<T> list, BigDecimal usdtTotal, BigDecimal total) {
        this.type = asset.getCode();
        this.name = asset.getName();
        this.list = list;
        this.usdtTotal = usdtTotal;
        this.total = total;
    }
}
