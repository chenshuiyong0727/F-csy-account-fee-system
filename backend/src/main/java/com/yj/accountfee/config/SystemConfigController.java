package com.yj.accountfee.config;

import com.yj.accountfee.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemConfigController {
    private final SystemConfigProperties properties;

    public SystemConfigController(SystemConfigProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/config")
    public ApiResult<SystemConfigVO> config() {
        return ApiResult.success(new SystemConfigVO(
            properties.getSystemName(),
            properties.getCompanyName(),
            properties.getLogoUrl(),
            properties.getLoginBackgroundUrl(),
            properties.getLoginSlogan()
        ));
    }
}
