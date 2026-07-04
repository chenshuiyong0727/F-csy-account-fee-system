package com.yj.accountfee.weight;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("weight_profile")
public class WeightProfile {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private LocalDate birthDate;
    private String gender;
    private BigDecimal heightCm;
    private LocalDate startDate;
    private BigDecimal startWeightJin;
    private BigDecimal targetWeightJin;
    private String shareToken;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
