package com.yj.accountfee.dashboard;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DashboardSummaryVO {
    private Long customerTotal;
    private Long normalCount;
    private Long warning60Count;
    private Long warning30Count;
    private Long warning5Count;
    private Long expiredCount;
    private Long noRecordCount;
    private BigDecimal monthAmount;
    private Long monthCount;
    private BigDecimal yearAmount;
    private Long yearCount;
}
