package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TransferRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *转出地址
	 */
	
	private String fromAddress;
	
	/**
	 *转入地址
	 */
	
	private String toAddress;
	
	/**
	 *交易币种
	 */
	
	private Long currencyId;
	
	/**
	 *交易数量
	 */
	
	private BigDecimal amount;
	
	/**
	 *手续费币种
	 */
	
	private Long feeCurrencyId;
	
	/**
	 *手续费数量
	 */
	
	private BigDecimal fee;
	
	/**
	 *状态 0=未确认，1=已确认
	 */
	
	private Integer status;
	
	/**
	 *交易哈希
	 */
	
	private String txHash;
	
	private Integer type;

	private Long height;

}
