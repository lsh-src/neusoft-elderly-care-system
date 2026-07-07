package com.neusoft.elderlycare.nursing.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.NursingLevel;
import com.neusoft.elderlycare.nursing.service.NursingLevelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nursing-levels")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE','USER')")
@Tag(name = "护理级别", description = "护理级别与收费标准")
public class NursingLevelController extends BaseCrudController<NursingLevel> {
    private final NursingLevelService levelService;

    @Override
    protected IService<NursingLevel> service() {
        return levelService;
    }

    @Override
    protected QueryWrapper<NursingLevel> wrapper(PageQuery query) {
        QueryWrapper<NursingLevel> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.like("level_name", query.getKeyword());
        }
        return w.orderByDesc("id");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public com.neusoft.elderlycare.common.ApiResponse<Boolean> create(@org.springframework.web.bind.annotation.RequestBody NursingLevel body) {
        return super.create(body);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public com.neusoft.elderlycare.common.ApiResponse<Boolean> update(@org.springframework.web.bind.annotation.PathVariable Long id,
                                                                       @org.springframework.web.bind.annotation.RequestBody NursingLevel body) {
        return super.update(id, body);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public com.neusoft.elderlycare.common.ApiResponse<Boolean> delete(@org.springframework.web.bind.annotation.PathVariable Long id) {
        return super.delete(id);
    }
}
