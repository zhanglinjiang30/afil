package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;


public enum TransferTypeEnum {

	TRNASFER("转账", 1),
	TRUST_GATEWAY("激活账号", 2),
	MINING("挖矿收益", 3),
	EXTRACT("提币", 4);

	private String name;
	private Integer code;

	TransferTypeEnum(String name, int code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public static String getName(Integer code) {
		if (code != null) {
			for (TransferTypeEnum e : TransferTypeEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getName();
				}
			}
		}
		return "";
	}

	public static List<SelectDto> getList() {
    	List<SelectDto> list = new ArrayList<SelectDto>();
		for (TransferTypeEnum e : TransferTypeEnum.values()) {
			list.add(new SelectDto(e.getCode(), e.getName()));
		}
		return list;
	}

}
