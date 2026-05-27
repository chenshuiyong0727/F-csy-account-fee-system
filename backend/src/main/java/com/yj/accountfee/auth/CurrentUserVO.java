package com.yj.accountfee.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUserVO {
    private String username;
    private String realName;
    private String roleCode;
}
