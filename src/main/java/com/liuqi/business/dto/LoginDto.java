package com.liuqi.business.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class LoginDto implements Serializable {

    private Long passphraseId;

    private String deviceId;
}
