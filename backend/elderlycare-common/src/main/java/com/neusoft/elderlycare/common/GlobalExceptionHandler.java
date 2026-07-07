package com.neusoft.elderlycare.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> business(BusinessException e) {
        return ApiResponse.fail(400, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> validation(Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException ex) {
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else {
            message = ((BindException) e).getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        }
        return ApiResponse.fail(400, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> denied() {
        return ApiResponse.fail(403, "无权限访问");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Void> duplicateKey(DuplicateKeyException e) {
        String message = "数据已存在，请勿重复添加";
        if (e.getMessage() != null) {
            String msg = e.getMessage();
            if (msg.contains("uk_phone")) {
                message = "该手机号已被注册";
            } else if (msg.contains("uk_room_bed")) {
                message = "该房间床位号已存在";
            } else if (msg.contains("uk_meal_no")) {
                message = "该膳食编号已存在";
            } else if (msg.contains("uk_customer_no")) {
                message = "该客户编号已存在";
            } else if (msg.contains("uk_register_no")) {
                message = "该登记编号已存在";
            } else if (msg.contains("uk_checkout_no")) {
                message = "该退住编号已存在";
            } else if (msg.contains("uk_outing_no")) {
                message = "该外出编号已存在";
            } else if (msg.contains("uk_record_no")) {
                message = "该记录编号已存在";
            } else if (msg.contains("uk_level_name")) {
                message = "该护理级别名称已存在";
            } else if (msg.contains("uk_area_name")) {
                message = "该区域名称已存在";
            }
        }
        return ApiResponse.fail(400, message);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Void> auth(AuthenticationException e) {
        return ApiResponse.fail(401, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> notReadable(HttpMessageNotReadableException e) {
        return ApiResponse.fail(400, "请求参数格式错误");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<Void> missingParam(MissingServletRequestParameterException e) {
        return ApiResponse.fail(400, "缺少参数: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Void> typeMismatch(MethodArgumentTypeMismatchException e) {
        return ApiResponse.fail(400, "参数类型错误: " + e.getName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Void> methodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ApiResponse.fail(405, "不支持的请求方法: " + e.getMethod());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<Void> notFound(NoResourceFoundException e) {
        return ApiResponse.fail(404, "接口不存在");
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    public ApiResponse<Void> lockTimeout(CannotAcquireLockException e) {
        log.warn("数据库锁等待超时: {}", e.getMessage());
        return ApiResponse.fail(503, "操作被占用，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> error(Exception e) {
        log.error("系统异常", e);
        return ApiResponse.fail(500, "系统内部错误，请稍后重试");
    }
}
