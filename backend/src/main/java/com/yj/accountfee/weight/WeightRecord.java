package com.yj.accountfee.weight;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("weight_record")
public class WeightRecord {
    private Long id;
    private Long profileId;
    private LocalDate recordDate;
    private BigDecimal weightJin;
    private BigDecimal bodyFatPercent;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
