package com.neusoft.elderlycare.dashboard.controller;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE')")
@Tag(name = "统计仪表盘", description = "运营数据统计")
public class DashboardController {
    private final DashboardService dashboardService;

    @Operation(summary = "运营数据统计", description = "获取仪表盘统计数据")
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.success(dashboardService.stats());
    }
}
