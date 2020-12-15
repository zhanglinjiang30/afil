package com.liuqi.business.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 最佳持币信息
 * @Date 15:11 2020/8/20
 */
@Data
@Builder
public class HoldingDto implements Serializable {

    private Long userId;

    private BigDecimal profit;// 收益

    private BigDecimal profitRatio;// 收益率

    private BigDecimal holding;// 持币量
}
