package com.yj.accountfee.weight;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class WeightBatchSaveDTO {
    @NotNull(message = "截止日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "间隔天数不能为空")
    @Min(value = 1, message = "间隔天数不能小于1")
    @Max(value = 365, message = "间隔天数不能超过365")
    private Integer intervalDays;

    @NotEmpty(message = "请填写批量体重")
    private List<@NotNull(message = "每个日期都要填写体重") BigDecimal> weights;

    private Boolean overwriteExisting = false;
}
