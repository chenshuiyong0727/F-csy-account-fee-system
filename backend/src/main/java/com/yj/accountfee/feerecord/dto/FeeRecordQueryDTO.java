package com.yj.accountfee.feerecord.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class FeeRecordQueryDTO {
    private Long customerId;
    private String companyName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDateStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDateEnd;
    private String payerName;
    private String payMethod;
    private String receiverName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate chargeEndDateStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate chargeEndDateEnd;
    private BigDecimal amountMin;
    private BigDecimal amountMax;
    private String dateType;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}
