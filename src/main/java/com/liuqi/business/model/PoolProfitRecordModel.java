package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PoolProfitRecordModel extends BaseModel{

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
	 *统计日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
	private Date statisticDate;
	
	/**
	 *挖矿收益【静态收益】
	 */
	
	private BigDecimal staticProfit;
	
	/**
	 *推广收益【动态收益】
	 */
	
	private BigDecimal dynamicProfit;
	
	/**
	 *持币收益率
	 */
	
	private BigDecimal profitRate;
	


}
