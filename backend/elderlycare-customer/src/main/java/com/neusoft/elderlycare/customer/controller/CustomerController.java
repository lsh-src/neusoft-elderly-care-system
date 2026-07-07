package com.neusoft.elderlycare.customer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE','USER')")
@Tag(name = "客户管理", description = "入住客户信息管理")
public class CustomerController extends BaseCrudController<Customer> {
    private final CustomerService customerService;

    @Override
    protected IService<Customer> service() {
        return customerService;
    }

    @Override
    protected QueryWrapper<Customer> wrapper(PageQuery query) {
        QueryWrapper<Customer> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.and(wrapper ->
                    wrapper.like("name", query.getKeyword())
                            .or()
                            .like("customer_no", query.getKeyword())
                            .or()
                            .like("phone", query.getKeyword())
            );
        }
        return w.orderByDesc("id");
    }

    @Operation(summary = "统计客户数量", description = "根据入住状态统计客户数量")
    @GetMapping("/count")
    public ApiResponse<Long> count(@RequestParam(required = false) Integer checkedIn) {
        if (checkedIn != null) {
            return ApiResponse.success(customerService.count(
                    new LambdaQueryWrapper<Customer>().eq(Customer::getCheckedIn, checkedIn)));
        }
        return ApiResponse.success(customerService.count());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Boolean> create(@org.springframework.web.bind.annotation.RequestBody Customer body) {
        return super.create(body);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Boolean> update(@org.springframework.web.bind.annotation.PathVariable Long id,
                                       @org.springframework.web.bind.annotation.RequestBody Customer body) {
        return super.update(id, body);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Boolean> delete(@org.springframework.web.bind.annotation.PathVariable Long id) {
        return super.delete(id);
    }
}