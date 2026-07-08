package com.neusoft.elderlycare.config;

import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeignAuthInterceptor JWT 转发测试")
class FeignAuthInterceptorTest {

    @InjectMocks
    private FeignAuthInterceptor interceptor;

    @Test
    @DisplayName("请求携带 Authorization 头时应转发给下游服务")
    void apply_withAuthHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.test-token");

        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getRequest()).thenReturn(request);

        RequestContextHolder.setRequestAttributes(attrs);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertTrue(template.headers().containsKey("Authorization"));
        assertEquals("Bearer eyJhbGciOiJIUzI1NiJ9.test-token",
                template.headers().get("Authorization").iterator().next());

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("请求无 Authorization 头时不应添加")
    void apply_withoutAuthHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getRequest()).thenReturn(request);

        RequestContextHolder.setRequestAttributes(attrs);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey("Authorization"));

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Authorization 头为空白时不应添加")
    void apply_blankAuthHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("   ");

        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getRequest()).thenReturn(request);

        RequestContextHolder.setRequestAttributes(attrs);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey("Authorization"));

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("RequestContextHolder 无属性时不应抛异常")
    void apply_noRequestContext() {
        RequestContextHolder.resetRequestAttributes();
        RequestTemplate template = new RequestTemplate();

        assertDoesNotThrow(() -> interceptor.apply(template));
        assertFalse(template.headers().containsKey("Authorization"));
    }

    @Test
    @DisplayName("Authorization 空字符串时不应添加")
    void apply_emptyAuthHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("");

        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getRequest()).thenReturn(request);

        RequestContextHolder.setRequestAttributes(attrs);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey("Authorization"));

        RequestContextHolder.resetRequestAttributes();
    }
}
