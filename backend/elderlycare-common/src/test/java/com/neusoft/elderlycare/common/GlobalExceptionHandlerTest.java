package com.neusoft.elderlycare.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler 全局异常处理测试")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    @DisplayName("BusinessException 应返回 400")
    void businessException() {
        BusinessException ex = new BusinessException("余额不足");
        ApiResponse<Void> resp = handler.business(ex);

        assertEquals(400, resp.getCode());
        assertEquals("余额不足", resp.getMessage());
    }

    @Test
    @DisplayName("AccessDeniedException 应返回 403 无权限访问")
    void accessDeniedException() {
        ApiResponse<Void> resp = handler.denied();

        assertEquals(403, resp.getCode());
        assertEquals("无权限访问", resp.getMessage());
    }

    @Test
    @DisplayName("AuthenticationException 应返回 401")
    void authenticationException() {
        BadCredentialsException ex = new BadCredentialsException("密码错误");
        ApiResponse<Void> resp = handler.auth(ex);

        assertEquals(401, resp.getCode());
        assertEquals("密码错误", resp.getMessage());
    }

    @Test
    @DisplayName("DuplicateKeyException uk_phone 应返回手机号已注册")
    void duplicateKey_phone() {
        DuplicateKeyException ex = new DuplicateKeyException("Duplicate entry '13800000000' for key 'uk_phone'");
        ApiResponse<Void> resp = handler.duplicateKey(ex);

        assertEquals(400, resp.getCode());
        assertEquals("该手机号已被注册", resp.getMessage());
    }

    @Test
    @DisplayName("DuplicateKeyException uk_room_bed 应返回房间床位号已存在")
    void duplicateKey_roomBed() {
        DuplicateKeyException ex = new DuplicateKeyException("Duplicate entry for key 'uk_room_bed'");
        ApiResponse<Void> resp = handler.duplicateKey(ex);

        assertEquals(400, resp.getCode());
        assertEquals("该房间床位号已存在", resp.getMessage());
    }

    @Test
    @DisplayName("DuplicateKeyException 未知约束应返回通用消息")
    void duplicateKey_unknown() {
        DuplicateKeyException ex = new DuplicateKeyException("Duplicate entry for key 'unknown_constraint'");
        ApiResponse<Void> resp = handler.duplicateKey(ex);

        assertEquals(400, resp.getCode());
        assertEquals("数据已存在，请勿重复添加", resp.getMessage());
    }

    @Test
    @DisplayName("DuplicateKeyException message 为 null 应返回通用消息")
    void duplicateKey_nullMessage() {
        // DuplicateKeyException 不允许 null message，用空字符串模拟
        DuplicateKeyException ex = new DuplicateKeyException("");
        ApiResponse<Void> resp = handler.duplicateKey(ex);

        assertEquals(400, resp.getCode());
        assertEquals("数据已存在，请勿重复添加", resp.getMessage());
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 应返回 400 请求参数格式错误")
    void notReadable() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ApiResponse<Void> resp = handler.notReadable(ex);

        assertEquals(400, resp.getCode());
        assertEquals("请求参数格式错误", resp.getMessage());
    }

    @Test
    @DisplayName("MissingServletRequestParameterException 应返回 400 缺少参数")
    void missingParam() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("phone", "String");
        ApiResponse<Void> resp = handler.missingParam(ex);

        assertEquals(400, resp.getCode());
        assertTrue(resp.getMessage().contains("phone"));
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 应返回 405")
    void methodNotSupported() {
        HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("PATCH");
        ApiResponse<Void> resp = handler.methodNotSupported(ex);

        assertEquals(405, resp.getCode());
        assertTrue(resp.getMessage().contains("PATCH"));
    }

    @Test
    @DisplayName("NoResourceFoundException 应返回 404")
    void notFound() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        ApiResponse<Void> resp = handler.notFound(ex);

        assertEquals(404, resp.getCode());
        assertEquals("接口不存在", resp.getMessage());
    }

    @Test
    @DisplayName("CannotAcquireLockException 应返回 503")
    void lockTimeout() {
        CannotAcquireLockException ex = new CannotAcquireLockException("Lock wait timeout");
        ApiResponse<Void> resp = handler.lockTimeout(ex);

        assertEquals(503, resp.getCode());
        assertEquals("操作被占用，请稍后重试", resp.getMessage());
    }

    @Test
    @DisplayName("未知 Exception 应返回 500")
    void unknownException() {
        Exception ex = new RuntimeException("unexpected");
        ApiResponse<Void> resp = handler.error(ex);

        assertEquals(500, resp.getCode());
        assertEquals("系统内部错误，请稍后重试", resp.getMessage());
    }

    @Test
    @DisplayName("BindException 应返回 400 并包含字段错误信息")
    void bindException() {
        BindException ex = mock(BindException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError("form", "name", "姓名不能为空"))
        );

        ApiResponse<Void> resp = handler.validation(ex);

        assertEquals(400, resp.getCode());
        assertTrue(resp.getMessage().contains("name"));
        assertTrue(resp.getMessage().contains("姓名不能为空"));
    }
}
