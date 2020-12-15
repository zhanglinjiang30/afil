package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 买卖
 */
public enum PublicOfferStatusEnum {

    UNOPENED("未开启", 0),
    ING("进行中", 1),
    OVER("已结束", 2);

    private String name;
    private Integer code;

    PublicOfferStatusEnum(String name, int code) {
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
            for (PublicOfferStatusEnum e : PublicOfferStatusEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (PublicOfferStatusEnum e : PublicOfferStatusEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}
