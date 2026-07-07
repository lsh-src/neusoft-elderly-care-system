package com.neusoft.elderlycare.nursing.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.NursingItem;
import com.neusoft.elderlycare.nursing.service.NursingItemService;
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
@RequestMapping("/nursing-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE','USER')")
@Tag(name = "护理内容", description = "护理项目管理")
public class NursingItemController extends BaseCrudController<NursingItem> {
    private final NursingItemService itemService;

    @Override
    protected IService<NursingItem> service() {
        return itemService;
    }

    @Override
    protected QueryWrapper<NursingItem> wrapper(PageQuery query) {
        QueryWrapper<NursingItem> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.like("item_name", query.getKeyword());
        }
        return w.orderByDesc("id");
    }
    // 🔥 重写分页：联查护理级别名称
    @Override
    @Operation(summary = "分页查询护理项目", description = "分页查询护理项目列表，含护理级别名称")
    @GetMapping("/page")
    public ApiResponse<IPage<NursingItem>> page(PageQuery query) {
        return ApiResponse.success(itemService.pageWithName(query));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Boolean> create(@org.springframework.web.bind.annotation.RequestBody NursingItem body) {
        return super.create(body);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Boolean> update(@org.springframework.web.bind.annotation.PathVariable Long id,
                                       @org.springframework.web.bind.annotation.RequestBody NursingItem body) {
        return super.update(id, body);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Boolean> delete(@org.springframework.web.bind.annotation.PathVariable Long id) {
        return super.delete(id);
    }
}
