package com.liuqi.business.dto;

import com.liuqi.business.model.UserPoolWalletModelDto;
import com.liuqi.business.model.UserWalletModelDto;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class RankStatisticDto implements Serializable{

    BigDecimal using;
    List<UserPoolWalletModelDto> list;
}
