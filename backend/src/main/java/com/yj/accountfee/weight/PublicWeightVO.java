package com.yj.accountfee.weight;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class PublicWeightVO {
    private String nickname;
    private String avatar;
    private BigDecimal heightCm;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal startWeight;
    private BigDecimal targetWeight;
    private BigDecimal todayWeight;
    private BigDecimal changeFromLast;
    private BigDecimal totalLoss;
    private BigDecimal distanceTarget;
    private BigDecimal bmi;
    private BigDecimal fat;
    private boolean fatEstimated;
    private long days;
    private BigDecimal progress;
    private List<PublicWeightRecordVO> records;
}
