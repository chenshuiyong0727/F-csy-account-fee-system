package com.yj.accountfee.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerSaveDTO {
    @NotBlank(message = "公司名称不能为空")
    private String companyName;
    private String taxNo;
    private String address;
    private String remark;
    private Integer status = 1;
}
