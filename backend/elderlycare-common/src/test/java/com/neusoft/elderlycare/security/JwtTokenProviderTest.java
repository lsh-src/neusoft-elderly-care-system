package com.neusoft.elderlycare.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider JWT 令牌测试")
class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        provider = new JwtTokenProvider();

        // 通过反射注入 @Value 字段
        Field secretField = JwtTokenProvider.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(provider, "test-secret-key-for-jwt-unit-testing-2026");

        Field expirationField = JwtTokenProvider.class.getDeclaredField("expirationMinutes");
        expirationField.setAccessible(true);
        expirationField.set(provider, 720L);

        // 触发 @PostConstruct
        provider.init();
    }

    @Test
    @DisplayName("生成的 token 应不为空且包含两個点号（三段式）")
    void generateToken_format() {
        String token = provider.generateToken("13800000000", "ROLE_ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(2, countDots(token), "JWT 应为三段式 (header.payload.signature)");
    }

    @Test
    @DisplayName("getPhone 应能从 token 中正确解析手机号")
    void getPhone_fromToken() {
        String token = provider.generateToken("13900000100", "ROLE_NURSE");

        String phone = provider.getPhone(token);

        assertEquals("13900000100", phone);
    }

    @Test
    @DisplayName("validate 应对有效 token 返回 true")
    void validate_validToken() {
        String token = provider.generateToken("13800000002", "ROLE_USER");

        assertTrue(provider.validate(token));
    }

    @Test
    @DisplayName("validate 应对篡改的 token 返回 false")
    void validate_tamperedToken() {
        String token = provider.generateToken("13800000000", "ROLE_ADMIN");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertFalse(provider.validate(tampered));
    }

    @Test
    @DisplayName("validate 应对空字符串返回 false")
    void validate_emptyString() {
        assertFalse(provider.validate(""));
    }

    @Test
    @DisplayName("validate 应对随机字符串返回 false")
    void validate_randomString() {
        assertFalse(provider.validate("not.a.jwt.token"));
    }

    @Test
    @DisplayName("同一用户多次生成 token 应不同（issuedAt 不同）")
    void generateToken_uniqueness() throws Exception {
        String t1 = provider.generateToken("13800000000", "ROLE_ADMIN");
        Thread.sleep(1100); // 确保秒级时间戳不同
        String t2 = provider.generateToken("13800000000", "ROLE_ADMIN");

        assertNotEquals(t1, t2, "不同时间生成的 token 应不同");
    }

    @Test
    @DisplayName("不同用户生成的 token 应不同")
    void generateToken_differentUsers() {
        String t1 = provider.generateToken("13800000000", "ROLE_ADMIN");
        String t2 = provider.generateToken("13800000001", "ROLE_MANAGER");

        assertNotEquals(t1, t2);
        assertEquals("13800000000", provider.getPhone(t1));
        assertEquals("13800000001", provider.getPhone(t2));
    }

    @Test
    @DisplayName("init 未调用时使用 token 应抛异常")
    void withoutInit_shouldThrow() {
        JwtTokenProvider uninit = new JwtTokenProvider();
        assertThrows(Exception.class, () -> uninit.generateToken("phone", "role"));
    }

    private int countDots(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == '.') count++;
        }
        return count;
    }
}
