package com.neusoft.elderlycare.checkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.Outing;
import com.neusoft.elderlycare.checkin.service.OutingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE','USER')")
@Tag(name = "外出登记", description = "客户外出与返回登记")
public class OutingController extends BaseCrudController<Outing> {
    private final OutingService outingService;

    @Override
    protected IService<Outing> service() {
        return outingService;
    }

    @Override
    protected QueryWrapper<Outing> wrapper(PageQuery query) {
        QueryWrapper<Outing> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.like("outing_no", query.getKeyword());
        }
        return w.orderByDesc("id");
    }

    @Operation(summary = "客户返回", description = "客户外出后返回登记")
    @PostMapping("/{id}/return")
    public ApiResponse<Boolean> returnBack(@Parameter(description = "外出记录ID", required = true, example = "1") @PathVariable Long id) {
        return ApiResponse.success(outingService.returnBack(id));
    }
    @Override
    @Operation(summary = "分页查询外出记录", description = "分页查询外出登记列表，含客户信息")
    @GetMapping("/page")
    public ApiResponse<IPage<Outing>> page(PageQuery query) {
        return ApiResponse.success(outingService.pageWithCustomer(query));
    }
}
