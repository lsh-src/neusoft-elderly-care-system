package com.neusoft.elderlycare.feign;

import com.neusoft.elderlycare.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * AI 服务 Feign 客户端
 */
@FeignClient(name = "elderlycare-ai", path = "/ai")
public interface AiFeignClient {

    @PostMapping("/chat")
    ApiResponse<String> chat(@RequestBody Map<String, String> request);

    @PostMapping("/rag/query")
    ApiResponse<String> ragQuery(@RequestBody Map<String, String> request);
}
