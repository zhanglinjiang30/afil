package com.liuqi.business.model;

import lombok.Data;


@Data
public class LockWalletUpdateLogModelDto extends LockWalletUpdateLogModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String userName;
	private String realName;
	private String currencyName;
	private String adminName;

}
