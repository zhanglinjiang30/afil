package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.CrowdfundStatusEnum;
import com.liuqi.business.enums.CrowdfundTypeEnum;
import lombok.Data;

import java.util.Date;

@Data
public class CrowdfundModelDto extends CrowdfundModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private CrowdfundInfoModelDto infoModelDto;

    private String currencyName;

    private String statusStr;

    public String getStatusStr() {
        return CrowdfundStatusEnum.getName(super.getStatus());
    }

    private String typeStr;

    public String getTypeStr() {
        return CrowdfundTypeEnum.getName(super.getType());
    }
}
