package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PublicOfferRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户地址ID
	 */
	
	private Long addressId;
	
	/**
	 *公募id
	 */
	
	private Long publicOfferId;
	
	/**
	 *数量
	 */
	
	private BigDecimal quantity;

	private Long currencyId;

	private BigDecimal unitPrice;

	private BigDecimal realPrice;


}
