package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class UserPoolWalletModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *可用数量
	 */
	
	private BigDecimal using;
	
	/**
	 *冻结数量
	 */
	
	private BigDecimal freeze;
	


}
