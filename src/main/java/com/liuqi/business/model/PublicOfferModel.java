package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PublicOfferModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *名称
	 */
	
	private String name;
	
	/**
	 *期数
	 */
	
	private Integer index;
	
	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *数量
	 */
	
	private BigDecimal quota;
	
	/**
	 *已用额度
	 */
	
	private BigDecimal useQuota;
	
	/**
	 *折扣比例
	 */
	
	private BigDecimal discountRate;
	
	/**
	 *开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date startTime;
	
	/**
	 *结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date overTime;
	
	/**
	 *状态 0 未开始 1 已开始 2 已结束
	 */
	
	private Integer status;
	


}
