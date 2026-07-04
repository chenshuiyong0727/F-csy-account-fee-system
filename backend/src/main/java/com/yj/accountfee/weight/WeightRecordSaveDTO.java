package com.yj.accountfee.weight;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class WeightRecordSaveDTO {
    @NotNull(message = "登记日期不能为空")
    private LocalDate recordDate;

    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "1", message = "体重必须大于0")
    @DecimalMax(value = "999.99", message = "体重不能超过999.99斤")
    private BigDecimal weightJin;

    @DecimalMin(value = "1", message = "体脂率不能小于1%")
    @DecimalMax(value = "75", message = "体脂率不能超过75%")
    private BigDecimal bodyFatPercent;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
