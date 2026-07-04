package com.yj.accountfee.weight;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class WeightProfileSaveDTO {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    @Size(max = 500, message = "头像地址不能超过500个字符")
    private String avatarUrl;

    @NotNull(message = "生日不能为空")
    private LocalDate birthDate;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "M|F", message = "性别值不正确")
    private String gender;

    @NotNull(message = "身高不能为空")
    @DecimalMin(value = "50", message = "身高不能小于50厘米")
    private BigDecimal heightCm;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "起始体重不能为空")
    @DecimalMin(value = "1", message = "起始体重必须大于0")
    private BigDecimal startWeightJin;

    @NotNull(message = "目标体重不能为空")
    @DecimalMin(value = "1", message = "目标体重必须大于0")
    private BigDecimal targetWeightJin;
}
