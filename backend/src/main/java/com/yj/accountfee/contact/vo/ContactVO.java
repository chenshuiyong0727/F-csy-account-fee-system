package com.yj.accountfee.contact.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ContactVO {
    private Long id;
    private Long customerId;
    private String contactName;
    private String contactPhone;
    private String contactWechat;
    private String positionName;
    private Integer isPrimary;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
