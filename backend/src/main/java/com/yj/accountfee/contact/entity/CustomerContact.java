package com.yj.accountfee.contact.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("acct_customer_contact")
public class CustomerContact {
    private Long id;
    private Long customerId;
    private String contactName;
    private String contactPhone;
    private String contactWechat;
    private String positionName;
    private Integer isPrimary;
    private String remark;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
