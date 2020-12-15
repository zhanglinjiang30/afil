package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 买卖
 */
public enum CrowdfundTypeEnum {

    SHARE("共享", 1),
    REAL_TIME("实时", 2)
    ;

    private String name;
    private Integer code;

    CrowdfundTypeEnum(String name, int code) {
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
            for (CrowdfundTypeEnum e : CrowdfundTypeEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (CrowdfundTypeEnum e : CrowdfundTypeEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}
