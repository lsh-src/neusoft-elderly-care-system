package com.neusoft.elderlycare.servicemodule.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.ServicePurchase;
import com.neusoft.elderlycare.servicemodule.service.ServicePurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-purchases")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
@Tag(name = "服务购买", description = "客户购买服务记录")
public class ServicePurchaseController extends BaseCrudController<ServicePurchase> {
    private final ServicePurchaseService purchaseService;

    @Override
    protected IService<ServicePurchase> service() {
        return purchaseService;
    }

    @Override
    protected QueryWrapper<ServicePurchase> wrapper(PageQuery query) {
        QueryWrapper<ServicePurchase> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.like("service_name", query.getKeyword());
        }
        return w.orderByDesc("id");
    }
    // 重写分页接口
    @Override
    @Operation(summary = "分页查询服务购买", description = "分页查询服务购买记录列表")
    @GetMapping("/page")
    public ApiResponse<IPage<ServicePurchase>> page(PageQuery query) {
        return ApiResponse.success(purchaseService.pageWithName(query));
    }

    @Operation(summary = "统计服务购买数量", description = "统计服务购买总数量")
    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(purchaseService.count());
    }
}
