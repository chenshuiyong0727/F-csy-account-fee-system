package com.yj.accountfee.common;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ApiResult<Void> handleBiz(BizException ex) {
        log.warn("业务异常：{}", ex.getMessage());
        return ApiResult.fail(500, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        log.warn("参数校验失败：{}", msg);
        return ApiResult.fail(400, msg);
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public ApiResult<Void> handleBind(Exception ex) {
        log.warn("请求参数绑定失败：{}", ex.getMessage());
        return ApiResult.fail(400, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handle(Exception ex) {
        log.error("系统异常", ex);
        return ApiResult.fail(500, ex.getMessage());
    }
}
