package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.PublicOfferStatusEnum;
import com.liuqi.utils.DateUtil;
import lombok.Data;

import java.util.Date;

@Data
public class PublicOfferModelDto extends PublicOfferModel {


    private Date startTimeStart;
    private Date startTimeEnd;

    private Date overTimeStart;
    private Date overTimeEnd;

    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";


    private String currencyName;

    private String statusStr;

    public String getStatusStr() {
        return PublicOfferStatusEnum.getName(super.getStatus());
    }

    private String startTimeStr;

    private String overTimeStr;

    public String getStartTimeStr() {
        return DateUtil.formatDate(super.getStartTime(),"yyyy-MM-dd HH:mm:ss");
    }

    public String getOverTimeStr() {
        return DateUtil.formatDate(super.getOverTime(),"yyyy-MM-dd HH:mm:ss");
    }
}
