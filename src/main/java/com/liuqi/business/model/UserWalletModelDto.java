package com.liuqi.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class UserWalletModelDto extends UserWalletModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

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
