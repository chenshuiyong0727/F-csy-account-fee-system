package com.yj.accountfee.feerecord.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FeeRecordVO {
    private Long id;
    private Long customerId;
    private String companyName;
    private LocalDate receiveDate;
    private String payerName;
    private Long payerContactId;
    private String payerContactName;
    private String payMethod;
    private String receiverName;
    private BigDecimal amount;
    private LocalDate chargeEndDate;
    private String remark;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
}
