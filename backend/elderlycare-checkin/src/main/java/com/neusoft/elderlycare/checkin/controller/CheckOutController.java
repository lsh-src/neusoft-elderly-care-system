package com.neusoft.elderlycare.checkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.CheckOut;
import com.neusoft.elderlycare.checkin.service.CheckOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check-outs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@Tag(name = "退住登记", description = "客户退住登记")
public class CheckOutController extends BaseCrudController<CheckOut> {
    private final CheckOutService checkOutService;

    @Override
    protected IService<CheckOut> service() {
        return checkOutService;
    }

    @Override
    protected QueryWrapper<CheckOut> wrapper(PageQuery query) {
        QueryWrapper<CheckOut> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.and(wrapper ->
                    wrapper.like("checkout_no", query.getKeyword())
                            .or()
                            .like("operator", query.getKeyword())
            );
        }
        return w.orderByDesc("id");
    }

    @Override
    @Operation(summary = "退住登记", description = "客户退住登记，自动释放床位")
    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody CheckOut body) {
        return ApiResponse.success(checkOutService.checkout(body));
    }
    // 🔥 核心修复：返回类型必须是 CheckOut，删除 NursingItem + 无效强转
    @Override
    @Operation(summary = "分页查询退住记录", description = "分页查询退住登记列表，含客户信息")
    @GetMapping("/page")
    public ApiResponse<IPage<CheckOut>> page(PageQuery query) {
        return ApiResponse.success(checkOutService.pageWithCustomer(query));
    }
}
