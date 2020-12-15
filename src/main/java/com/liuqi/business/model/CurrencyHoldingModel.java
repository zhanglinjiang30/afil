package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CurrencyHoldingModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *最低持币数量
	 */
	
	private BigDecimal minHolding;
	
	/**
	 *最佳持币数量
	 */
	
	private BigDecimal niceHolding;
	
	/**
	 *最佳持币的收益
	 */
	
	private BigDecimal niceProfit;
	


}
