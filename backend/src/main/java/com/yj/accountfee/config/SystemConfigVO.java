package com.yj.accountfee.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemConfigVO {
    private String systemName;
    private String companyName;
    private String logoUrl;
    private String loginBackgroundUrl;
    private String loginSlogan;
}
