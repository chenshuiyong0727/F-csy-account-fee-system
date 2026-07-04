package com.yj.accountfee.weight;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicWeightRecordVO {
    private LocalDate date;
    private BigDecimal weight;
    private BigDecimal fat;
    private boolean fatEstimated;
}
