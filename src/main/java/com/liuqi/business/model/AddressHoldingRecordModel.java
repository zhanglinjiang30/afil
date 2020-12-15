package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class AddressHoldingRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *设备号
	 */
	
	private String deviceId;
	
	/**
	 *地址表id
	 */
	
	private Long addressId;

	/**
	 * 交易密码
	 */
	private String tradePwd;

	/**
	 * 钱包名称
	 */
	private String name;

	/**
	 * 是否为主地址 1=是  0=否
	 */
	private Integer main;

	/**
	 * 是否显示 1=是  0=否
	 */
	private Integer display;

}
