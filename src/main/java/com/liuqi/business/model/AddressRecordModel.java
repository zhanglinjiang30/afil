package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class AddressRecordModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *私钥表id
	 */
	
	private Long passphraseId;
	
	/**
	 *地址序号
	 */
	
	private Integer index;
	
	/**
	 *地址
	 */
	
	private String address;
	
	/**
	 *私钥
	 */
	
	private String privateKey;
	
	/**
	 *首次创建的设备号
	 */
	
	private String deviceId;

	/**
	 * 预激活的地址id
	 */
	private Long preActiveAid;

	/**
	 * 地址是否被激活 0=未激活，1=激活中，2=已激活
	 */

	private Integer active;

	/**
	 * 空投SUC数量
	 */

	private BigDecimal sucAmount;

	/**
	 *激活地址总数量
	 */

	private Integer activeCount;

	/**
	 *激活设备总数量
	 */

	private Integer activeDevice;

	/**
	 * 投票等级
	 */
	private Integer crowdfundLevel;

	/**
	 * 矿池等级
	 */
	private Integer poolLevel;

	/**
	 * 是否otc发布
	 */
	private Integer otc;
}
