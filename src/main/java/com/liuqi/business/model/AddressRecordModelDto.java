package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.PoolLevelEnum;
import com.liuqi.business.enums.YesNoEnum;
import lombok.Data;

import java.util.Date;

@Data
public class AddressRecordModelDto extends AddressRecordModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String passphrase;

    private String poolLevelStr;

    public String getPoolLevelStr() {
        return PoolLevelEnum.getName(super.getPoolLevel());
    }

    private String tradePwd;

    private String otcStr;

    public String getOtcStr() {
        return YesNoEnum.getName(super.getOtc());
    }
}
