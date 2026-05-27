package com.yj.accountfee.auth;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthSession {
    private String username;
    private String realName;
    private String roleCode;
    private LocalDateTime expireTime;
}
