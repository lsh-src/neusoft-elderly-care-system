package com.neusoft.elderlycare.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiResponse 统一响应测试")
class ApiResponseTest {

    @Test
    @DisplayName("success(data) 应返回 code=200, message=success, data=传入值")
    void success_withData() {
        Map<String, Object> payload = Map.of("name", "张三", "age", 68);
        ApiResponse<Map<String, Object>> resp = ApiResponse.success(payload);

        assertEquals(200, resp.getCode());
        assertEquals("success", resp.getMessage());
        assertNotNull(resp.getData());
        assertEquals("张三", resp.getData().get("name"));
        assertEquals(68, resp.getData().get("age"));
    }

    @Test
    @DisplayName("success() 无参应返回 code=200, data=null")
    void success_noData() {
        ApiResponse<Void> resp = ApiResponse.success();

        assertEquals(200, resp.getCode());
        assertEquals("success", resp.getMessage());
        assertNull(resp.getData());
    }

    @Test
    @DisplayName("fail() 应返回指定 code 和 message, data=null")
    void fail_withCodeAndMessage() {
        ApiResponse<Void> resp = ApiResponse.fail(403, "无权限访问");

        assertEquals(403, resp.getCode());
        assertEquals("无权限访问", resp.getMessage());
        assertNull(resp.getData());
    }

    @Test
    @DisplayName("success(null) 应正常返回 data=null")
    void success_withNullData() {
        ApiResponse<String> resp = ApiResponse.success(null);

        assertEquals(200, resp.getCode());
        assertNull(resp.getData());
    }

    @Test
    @DisplayName("fail() 不同错误码应正确区分")
    void fail_differentCodes() {
        ApiResponse<Void> r400 = ApiResponse.fail(400, "参数错误");
        ApiResponse<Void> r401 = ApiResponse.fail(401, "未认证");
        ApiResponse<Void> r404 = ApiResponse.fail(404, "不存在");
        ApiResponse<Void> r500 = ApiResponse.fail(500, "系统错误");

        assertEquals(400, r400.getCode());
        assertEquals(401, r401.getCode());
        assertEquals(404, r404.getCode());
        assertEquals(500, r500.getCode());
    }

    @Test
    @DisplayName("Lombok 生成的 getter/setter 应正常工作")
    void lombokGettersSetters() {
        ApiResponse<String> resp = new ApiResponse<>();
        resp.setCode(200);
        resp.setMessage("ok");
        resp.setData("hello");

        assertEquals(200, resp.getCode());
        assertEquals("ok", resp.getMessage());
        assertEquals("hello", resp.getData());
    }

    @Test
    @DisplayName("全参构造器应正常工作")
    void allArgsConstructor() {
        ApiResponse<Integer> resp = new ApiResponse<>(200, "success", 42);

        assertEquals(200, resp.getCode());
        assertEquals("success", resp.getMessage());
        assertEquals(42, resp.getData());
    }
}
