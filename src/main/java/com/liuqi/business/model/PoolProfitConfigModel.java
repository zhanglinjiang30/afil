package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PoolProfitConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *等级
	 */
	
	private Integer level;
	
	/**
	 *直推数量
	 */
	
	private Integer directCount;
	
	/**
	 *小区有效账户
	 */
	
	private Integer validCount;
	
	/**
	 *奖励比例
	 */
	
	private BigDecimal profitRate;
	
	/**
	 *最低下代数量
	 */
	
	private Integer minSubCount;
	
	/**
	 *最高下代数量
	 */
	
	private Integer maxSubCount;
	
	/**
	 *状态 0 禁用 1 启用
	 */
	
	private Integer status;

	/**
	 * 公募奖励数量
	 */
	private BigDecimal publicOfferProfitAmount;
	


}
