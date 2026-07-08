package com.neusoft.elderlycare.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BusinessException 业务异常测试")
class BusinessExceptionTest {

    @Test
    @DisplayName("应正确传递错误消息")
    void messagePassed() {
        BusinessException ex = new BusinessException("余额不足");
        assertEquals("余额不足", ex.getMessage());
    }

    @Test
    @DisplayName("应是 RuntimeException 的子类")
    void isRuntimeException() {
        BusinessException ex = new BusinessException("test");
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("空消息应正常工作")
    void emptyMessage() {
        BusinessException ex = new BusinessException("");
        assertEquals("", ex.getMessage());
    }

    @Test
    @DisplayName("中文消息应正确传递")
    void chineseMessage() {
        BusinessException ex = new BusinessException("该手机号已被注册");
        assertEquals("该手机号已被注册", ex.getMessage());
    }

    @Test
    @DisplayName("可以被 try-catch 捕获")
    void catchable() {
        assertThrows(BusinessException.class, () -> {
            throw new BusinessException("test error");
        });
    }

    @Test
    @DisplayName("catch 后能获取原始消息")
    void messagePreservedAfterCatch() {
        try {
            throw new BusinessException("操作失败");
        } catch (BusinessException e) {
            assertEquals("操作失败", e.getMessage());
        }
    }
}
