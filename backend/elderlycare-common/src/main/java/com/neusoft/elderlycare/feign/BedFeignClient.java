package com.neusoft.elderlycare.feign;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.entity.Bed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 床位服务 Feign 客户端
 */
@FeignClient(name = "elderlycare-bed", path = "/beds")
public interface BedFeignClient {

    @GetMapping("/{id}")
    ApiResponse<Bed> getById(@PathVariable("id") Long id);

    @GetMapping("/stats")
    ApiResponse<Map<String, Long>> statusStats();

    @PutMapping("/{id}")
    ApiResponse<Boolean> updateById(@PathVariable("id") Long id, @RequestBody Bed bed);
}
