package com.neusoft.elderlycare.feign;

import com.neusoft.elderlycare.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 服务购买 Feign 客户端
 */
@FeignClient(name = "elderlycare-service", path = "/service-purchases")
public interface ServicePurchaseFeignClient {

    @GetMapping("/count")
    ApiResponse<Long> count();
}
