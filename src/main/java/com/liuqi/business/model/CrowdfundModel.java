package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CrowdfundModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *名称
	 */
	
	private String name;

	/**
	 *状态 0 禁用 1启用
	 */
	
	private Integer status;

	/**
	 * 币种id
	 */
	private Long currencyId;

	/**
	 * 类型
	 */
	private Integer type;
}
