package com.yj.accountfee.contact.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactSaveDTO {
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;
    private String contactPhone;
    private String contactWechat;
    private String positionName;
    private Integer isPrimary = 0;
    private String remark;
}
