package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserOtcWalletModelDto extends UserOtcWalletModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String sucAddress;
    private String currencyName;
    private String opePwd;

    /**
     *提币开关
     */
    private boolean extractSwitch;

    /**
     *充值开关
     */
    private boolean rechargeSwitch;

    /**
     * 总数量(可用加冻结)
     */
    private BigDecimal total;

    private BigDecimal price;

    private List<Long> currencyList;

    private Integer position;

    private String currencyImage;


    private BigDecimal minAmount = BigDecimal.ZERO;// 最低数量

    private BigDecimal maxAmount = BigDecimal.ZERO;// 最高数量

    private BigDecimal cny;
}
