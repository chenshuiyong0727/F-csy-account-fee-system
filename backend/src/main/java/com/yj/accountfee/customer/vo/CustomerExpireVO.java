package com.yj.accountfee.customer.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CustomerExpireVO {
    private Long customerId;
    private String companyName;
    private String taxNo;
    private String address;
    private String remark;
    private LocalDate latestChargeEndDate;
    private Integer remainDays;
    private String expireStatus;
    private String primaryContactName;
    private String primaryContactPhone;
    private String primaryContactWechat;
    private LocalDate latestReceiveDate;
    private BigDecimal latestReceiveAmount;
}
