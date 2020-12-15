package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;


public enum UserWalletTypeEnum {

    USING("币币账户", 1),
    CURRENCY("法币账户", 2),
    POOL("矿池账户", 3)
    ;

    private String name;
    private Integer code;

    UserWalletTypeEnum(String name, int code) {
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
            for (UserWalletTypeEnum e : UserWalletTypeEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static UserWalletTypeEnum getEnum(Integer code) {
        if (code != null) {
            for (UserWalletTypeEnum e : UserWalletTypeEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e;
                }
            }
        }
        return null;
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (UserWalletTypeEnum e : UserWalletTypeEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}
