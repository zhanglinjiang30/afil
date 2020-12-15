package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CrowdfundRewardConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *等级
	 */
	
	private Integer level;
	
	/**
	 *直推人数
	 */
	
	private Integer directCount;
	
	/**
	 *小区总持币数量
	 */
	
	private BigDecimal totalAmount;
	
	/**
	 *奖励比例
	 */
	
	private BigDecimal rewardRate;
	
	/**
	 *最低下级数量
	 */
	
	private Integer minSubCount;
	
	/**
	 *最高下级数量
	 */
	
	private Integer maxSubCount;
	
	/**
	 *状态 0 禁用，1 启用
	 */
	
	private Integer status;

	/**
	 * 币种ID
	 */
	private Long currencyId;


}
