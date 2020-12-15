package com.liuqi.business.dto;

import com.liuqi.business.model.HelpModelDto;

import java.io.Serializable;
import java.util.List;

public class HelpDto implements Serializable {
    private String typeName;
    private List<HelpModelDto> list;

    public HelpDto() {
    }

    public HelpDto(String typeName, List<HelpModelDto> list) {
        this.typeName = typeName;
        this.list = list;
    }
}
