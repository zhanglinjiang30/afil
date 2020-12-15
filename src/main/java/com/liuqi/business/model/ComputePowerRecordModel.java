package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ComputePowerRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *隶属于哪个地址id
	 */
	
	private Long pUserId;
	
	/**
	 *连带自己的算力
	 */
	
	private BigDecimal power;
	
	/**
	 *是否大区0=否 1=是
	 */
	
	private Integer largeZone;
	


}
