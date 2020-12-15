package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PoolStatisticModel extends BaseModel{

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
	 *日收益
	 */
	
	private BigDecimal dailyProfit;
	
	/**
	 *累计收益
	 */
	
	private BigDecimal totalProfit;
	
	/**
	 *自己的算力
	 */
	
	private BigDecimal computePower;
	
	/**
	 *推广的算力
	 */
	
	private BigDecimal inviteComputePower;
	


}
