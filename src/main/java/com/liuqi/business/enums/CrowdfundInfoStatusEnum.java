package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 买卖
 */
public enum CrowdfundInfoStatusEnum {

    UNOPENED("未开启", 0),
    ING("进行中", 1),
    OVER("已结束", 2),
    SETTLEMENT("已结算", 3);

    private String name;
    private Integer code;

    CrowdfundInfoStatusEnum(String name, int code) {
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
            for (CrowdfundInfoStatusEnum e : CrowdfundInfoStatusEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (CrowdfundInfoStatusEnum e : CrowdfundInfoStatusEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}
