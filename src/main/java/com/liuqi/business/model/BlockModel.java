package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class BlockModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *高度
	 */
	
	private Long height;
	
	/**
	 *交易数量
	 */
	
	private Integer transactionCount;
	
	/**
	 *区块奖励
	 */
	
	private BigDecimal reward;
	
	/**
	 *区块哈希
	 */
	
	private String hash;
	


}
