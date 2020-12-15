package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.CrowdfundInfoStatusEnum;
import com.liuqi.utils.DateUtil;
import lombok.Data;

@Data
public class CrowdfundInfoModelDto extends CrowdfundInfoModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String statusStr;

    public String getStatusStr() {
        return CrowdfundInfoStatusEnum.getName(super.getStatus());
    }

    private String startTimeStr;

    private String endTimeStr;

    public String getStartTimeStr() {
        return DateUtil.formatDate(super.getStartTime(),"yyyy-MM-dd HH:mm:ss");
    }

    public String getEndTimeStr() {
        return DateUtil.formatDate(super.getEndTime(),"yyyy-MM-dd HH:mm:ss");
    }
}
