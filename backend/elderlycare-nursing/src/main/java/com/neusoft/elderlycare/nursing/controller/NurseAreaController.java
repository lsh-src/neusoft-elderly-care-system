package com.neusoft.elderlycare.nursing.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.NurseArea;
import com.neusoft.elderlycare.nursing.service.NurseAreaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nurse-areas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@Tag(name = "负责区域", description = "护理区域管理")
public class NurseAreaController extends BaseCrudController<NurseArea> {

    private final NurseAreaService nurseAreaService;

    // 【修复】严格返回 IService<T>
    @Override
    protected IService<NurseArea> service() {
        return nurseAreaService;
    }

    @Override
    protected QueryWrapper<NurseArea> wrapper(PageQuery query) {
        QueryWrapper<NurseArea> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.like("area_name", query.getKeyword());
        }
        return w.orderByDesc("id");
    }
}