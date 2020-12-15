package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 买卖
 */
public enum PoolLevelEnum {

    level_0("普通会员", 0),
    level_1("A1", 1),
    level_2("A2", 2),
    level_3("A3", 3),
    level_4("A4", 4),
    level_5("A4", 5),
    level_6("A4", 6);

    private String name;
    private Integer code;

    PoolLevelEnum(String name, int code) {
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
            for (PoolLevelEnum e : PoolLevelEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (PoolLevelEnum e : PoolLevelEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}
