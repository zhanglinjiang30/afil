package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class AirdropRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *地址id
	 */
	
	private Long addressId;
	
	/**
	 *空投数量SUC
	 */
	
	private BigDecimal sucAmount;
	
	/**
	 *消耗的USDT数量
	 */
	
	private BigDecimal usdtAmount;
	


}
