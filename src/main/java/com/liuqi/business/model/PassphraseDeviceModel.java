package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PassphraseDeviceModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *助记词
	 */
	
	private String passphrase;
	
	/**
	 *设备号
	 */
	
	private String deviceId;

	/**
	 *激活地址总数量
	 */

	private Integer activeCount;

	/**
	 *激活设备总数量
	 */

	private Integer activeDevice;

}
