package com.yj.accountfee.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "account-fee.system")
public class SystemConfigProperties {
    private String systemName = "会计服务费收款登记系统";
    private String companyName = "轻舟财税服务中心";
    private String logoUrl = "/brand/logo.svg";
    private String loginBackgroundUrl = "/brand/login-bg.png";
    private String loginSlogan = "客户服务费收款、到期提醒与台账导出，一处登记，清楚可查。";
}
