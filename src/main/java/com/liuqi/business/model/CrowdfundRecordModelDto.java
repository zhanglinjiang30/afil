package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CrowdfundRecordModelDto extends CrowdfundRecordModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String userName;

    private String crowdfundName;

    private Integer index;

    private Integer crowdfundType;

    private String currencyName;

    private Integer infoStatus;
}
