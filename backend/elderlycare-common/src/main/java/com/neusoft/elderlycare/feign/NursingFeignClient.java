package com.neusoft.elderlycare.feign;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.entity.NursingRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 护理服务 Feign 客户端
 */
@FeignClient(name = "elderlycare-nursing", path = "/nursing-records")
public interface NursingFeignClient {

    @GetMapping("/customer/{customerId}")
    ApiResponse<List<NursingRecord>> getByCustomerId(@PathVariable("customerId") Long customerId);

    @GetMapping("/list")
    ApiResponse<List<NursingRecord>> listAll();
}
