package com.yj.accountfee.customer.dto;

import lombok.Data;

@Data
public class CustomerQueryDTO {
    private String companyName;
    private Integer status;
    private String expireStatus;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}
