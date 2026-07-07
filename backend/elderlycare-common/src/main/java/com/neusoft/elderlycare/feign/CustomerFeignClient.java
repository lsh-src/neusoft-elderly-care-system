package com.neusoft.elderlycare.feign;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.entity.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 客户服务 Feign 客户端
 */
@FeignClient(name = "elderlycare-customer", path = "/customers")
public interface CustomerFeignClient {

    @GetMapping("/{id}")
    ApiResponse<Customer> getById(@PathVariable("id") Long id);

    @GetMapping("/count")
    ApiResponse<Long> count(@RequestParam(required = false) Integer checkedIn);

    @PutMapping("/{id}")
    ApiResponse<Boolean> updateById(@PathVariable("id") Long id, @RequestBody Customer customer);
}
