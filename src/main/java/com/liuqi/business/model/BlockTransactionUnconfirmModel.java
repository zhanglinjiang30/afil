package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class BlockTransactionUnconfirmModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *矿工费(SUC)
	 */
	
	private BigDecimal fee;
	
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
	 * 0=未确认，1=已确认
	 */
	private Integer confirm;

	/**
	 * 外部id
	 */
	private Long exId;

	/**
	 * 交易哈希
	 */
	private String txHash;
}
