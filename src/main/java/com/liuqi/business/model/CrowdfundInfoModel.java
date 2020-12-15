package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CrowdfundInfoModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *投票id
	 */
	
	private Long crowdfundId;
	
	/**
	 *总额度
	 */
	
	private BigDecimal totalQuota;
	
	/**
	 *单笔最低额度
	 */

	private BigDecimal minQuota;

	/**
	 *单笔最高额度
	 */

	private BigDecimal maxQuota;

	/**
	 *已用额度
	 */
	
	private BigDecimal usedQuota;
	
	/**
	 *返回本金比例
	 */
	
	private BigDecimal refundRate;
	
	/**
	 *静态奖励比例
	 */
	
	private BigDecimal staticRewardRate;
	
	/**
	 *直推奖励比例
	 */
	
	private BigDecimal directRewardRate;
	
	/**
	 *状态 1 进行中 2 已结束
	 */
	
	private Integer status;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date endTime;


	/**
	 *第几期
	 */

	private Integer index;

}
