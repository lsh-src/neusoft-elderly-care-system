package com.neusoft.elderlycare.servicemodule.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.ServiceRelation;
import com.neusoft.elderlycare.servicemodule.service.ServiceRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-relations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@Tag(name = "服务对象", description = "客户与健康管家关联")
public class ServiceRelationController extends BaseCrudController<ServiceRelation> {
    private final ServiceRelationService relationService;

    @Override
    protected IService<ServiceRelation> service() {
        return relationService;
    }

    @Override
    protected QueryWrapper<ServiceRelation> wrapper(PageQuery query) {
        QueryWrapper<ServiceRelation> w = new QueryWrapper<>();
        // 客户姓名是非数据库字段，无法直接搜索
        return w.orderByDesc("id");
    }
    // 🔥 修复：返回类型必须是 ServiceRelation（和泛型一致），删除错误的 NursingItem
    @Override
    @Operation(summary = "分页查询服务关系", description = "分页查询客户与健康管家关联列表")
    @GetMapping("/page")
    public ApiResponse<IPage<ServiceRelation>> page(PageQuery query) {
        return ApiResponse.success(relationService.pageWithName(query));
    }
}
