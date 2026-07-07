package com.neusoft.elderlycare.feign;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.entity.CheckIn;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 入住服务 Feign 客户端
 */
@FeignClient(name = "elderlycare-checkin", path = "/check-ins")
public interface CheckInFeignClient {

    @GetMapping("/count")
    ApiResponse<Long> count(@RequestParam(required = false) String date);

    @GetMapping("/list")
    ApiResponse<List<CheckIn>> listAll();
}
