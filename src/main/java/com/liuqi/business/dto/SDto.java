package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SDto implements Serializable{

    private Integer activeDeviceCount;

    private Integer activeAddressCount;
}
