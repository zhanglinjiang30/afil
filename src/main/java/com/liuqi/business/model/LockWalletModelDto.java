package com.liuqi.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LockWalletModelDto extends LockWalletModel{


    private String userName;
    private String currencyName;

    private String opePwd;

    private List<Long> currencyList;

    private BigDecimal price;
    private Integer position;
}
