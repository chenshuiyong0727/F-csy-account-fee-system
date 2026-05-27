package com.yj.accountfee.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }

    public static ApiResult<Void> success() {
        return new ApiResult<>(200, "success", null);
    }

    public static ApiResult<Void> fail(Integer code, String msg) {
        return new ApiResult<>(code, msg, null);
    }
}
