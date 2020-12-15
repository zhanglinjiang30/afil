package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.WalletLogTypeEnum;
import lombok.Data;

import java.util.Date;

@Data
public class UserOtcWalletLogModelDto extends UserOtcWalletLogModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String userName;
    private String currencyName;
    private String typeStr;


    public String getTypeStr() {
        if(super.getType()!=null){
            typeStr= WalletLogTypeEnum.getName(super.getType());
        }
        return typeStr;
    }
}
