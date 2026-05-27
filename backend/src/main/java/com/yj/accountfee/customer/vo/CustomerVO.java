package com.yj.accountfee.customer.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CustomerVO {
    private Long id;
    private String companyName;
    private String taxNo;
    private String address;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
