package com.neusoft.elderlycare.checkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.CheckIn;
import com.neusoft.elderlycare.checkin.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/check-ins")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@Tag(name = "入住登记", description = "客户入住登记")
public class CheckInController extends BaseCrudController<CheckIn> {
    private final CheckInService checkInService;

    @Override
    protected IService<CheckIn> service() {
        return checkInService;
    }

    @Override
    protected QueryWrapper<CheckIn> wrapper(PageQuery query) {
        QueryWrapper<CheckIn> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like("register_no", query.getKeyword()).or().like("operator", query.getKeyword());
        }
        return wrapper.orderByDesc("id");
    }

    @Override
    @Operation(summary = "入住登记", description = "客户入住登记，自动分配床位")
    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody CheckIn body) {
        return ApiResponse.success(checkInService.register(body));
    }
    // 🔥 核心修复：返回类型严格匹配 CheckIn，删除错误的 NursingItem
    @Override
    @Operation(summary = "分页查询入住记录", description = "分页查询入住登记列表，含客户信息")
    @GetMapping("/page")
    public ApiResponse<IPage<CheckIn>> page(PageQuery query) {
        return ApiResponse.success(checkInService.pageWithRelation(query));
    }

    @Operation(summary = "按日期统计入住数量", description = "统计指定日期的入住记录数量")
    @GetMapping("/count")
    public ApiResponse<Long> count(@RequestParam(required = false) String date) {
        if (date != null && !date.isBlank()) {
            return ApiResponse.success(checkInService.count(
                    new LambdaQueryWrapper<CheckIn>().eq(CheckIn::getCheckInDate, LocalDate.parse(date))));
        }
        return ApiResponse.success(checkInService.count());
    }

    @Operation(summary = "查询所有入住记录", description = "获取全部入住记录列表（供内部Feign调用）")
    @GetMapping("/list")
    public ApiResponse<List<CheckIn>> list() {
        return ApiResponse.success(checkInService.list());
    }
}
