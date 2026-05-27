package com.yj.accountfee.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("acct_customer")
public class Customer {
    private Long id;
    private String companyName;
    private String taxNo;
    private String address;
    private String remark;
    private Integer status;
    private Integer deleted;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
}
