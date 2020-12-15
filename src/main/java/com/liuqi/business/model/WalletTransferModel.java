package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class WalletTransferModel extends BaseModel{

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
	 *数量
	 */
	
	private BigDecimal quantity;
	
	/**
	 *来源钱包 1资产 2 法币 3 矿池
	 */
	
	private Integer source;
	
	/**
	 *目标钱包 1资产 2 法币 3 矿池
	 */
	
	private Integer target;
	
	/**
	 *状态 1未处理 2成功 3拒绝 4失败
	 */
	
	private Integer status;
	


}
