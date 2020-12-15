package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 确认状态
 */
public enum ConfirmStatusEnum {

    UNCONFIRM("未确认", 0),
    CONFIRMED("已确认", 1);

    private String name;
    private Integer code;

    ConfirmStatusEnum(String name, int code) {
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
            for (ConfirmStatusEnum e : ConfirmStatusEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (ConfirmStatusEnum e : ConfirmStatusEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}
