package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ActiveRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *激活方助记词id
	 */

	private Long fromPId;

	/**
	 *激活方地址id
	 */
	
	private Long fromAddressId;
	
	/**
	 *被激活地址id
	 */
	
	private Long toAddressId;
	
	/**
	 *被激活时地址所处设备
	 */
	
	private String activeDevice;

	/**
	 *被激活地址标记的名称
	 */
	private String name;

}
