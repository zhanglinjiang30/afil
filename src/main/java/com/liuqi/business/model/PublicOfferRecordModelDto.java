package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
public class PublicOfferRecordModelDto extends PublicOfferRecordModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String userName;

    private String publicOfferName;

    private String address;

    private Integer publicOfferStatus;
}
