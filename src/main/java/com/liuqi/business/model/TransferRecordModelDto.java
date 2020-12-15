package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.ConfirmStatusEnum;
import com.liuqi.business.enums.TradeStatusEnum;
import com.liuqi.business.enums.TransferTypeEnum;
import lombok.Data;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;

import java.util.Date;

@Data
public class TransferRecordModelDto extends TransferRecordModel {


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private String currencyName;

    private String feeCurrencyName;

    private String searchAddress;

    private String statusStr;

    public String getStatusStr() {
        return ConfirmStatusEnum.getName(getStatus());
    }

    private String typeStr;

    public String getTypeStr() {
        return TransferTypeEnum.getName(getType());
    }

}
