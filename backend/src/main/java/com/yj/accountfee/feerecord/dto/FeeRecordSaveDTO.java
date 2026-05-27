package com.yj.accountfee.feerecord.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class FeeRecordSaveDTO {
    @NotNull(message = "客户公司不能为空")
    private Long customerId;

    @NotNull(message = "收款日期不能为空")
    private LocalDate receiveDate;

    private String payerName;
    private Long payerContactId;

    @NotBlank(message = "收款方式不能为空")
    private String payMethod;

    @NotBlank(message = "收款人不能为空")
    private String receiverName;

    @NotNull(message = "收款金额不能为空")
    @DecimalMin(value = "0.00", message = "收款金额不能小于0")
    private BigDecimal amount;

    @NotNull(message = "收费到日期不能为空")
    private LocalDate chargeEndDate;

    private String remark;
}
