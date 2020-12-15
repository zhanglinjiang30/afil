package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class RobotModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *订单用户id
	 */
	private Long userId;
	
	/**
	 *交易对id
	 */
	private Long tradeId;
	
	/**
	 *类型 0内部价格 1第三方价格
	 */
	private Integer type;
	
	/**
	 *单据类型 0真实交易  1虚拟交易
	 */
	private Integer runType;

	/**
	 *最小交易量
	 */
	private BigDecimal minQuantity;
	
	/**
	 *最大交易量
	 */
	private BigDecimal maxQuantity;
	
	/**
	 *间隔时间
	 */
	private Integer interval;
	/**
	 * 间隔价格
	 */
	private BigDecimal intervalPrice;

	/**
	 * 涨跌%
	 */
	private BigDecimal rate;
	/**
	 * 升降  0升 1降
	 */
	private Integer upDown;

	//买开关 0关1开
	private Integer  buySwitch;
	//卖开关 0关1开
	private Integer  sellSwitch;
	//钱包开关 0关 1开
	private Integer walletSwitch;
}
