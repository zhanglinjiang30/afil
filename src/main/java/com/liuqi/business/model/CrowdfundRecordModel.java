package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CrowdfundRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *地址id
	 */
	
	private Long addressId;
	
	/**
	 *投票详情id
	 */
	
	private Long crowdfundInfoId;
	
	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *数量
	 */
	
	private BigDecimal quantity;


	/**
	 * 获得币种ID
	 */
	private Long gainCurrencyId;

	/**
	 * 获得币种数量
	 */
	private BigDecimal gainQuantity;

	/**
	 * 单价
	 */
	private BigDecimal unitPrice;

}
