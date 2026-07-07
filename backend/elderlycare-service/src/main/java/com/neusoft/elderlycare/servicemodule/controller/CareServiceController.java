package com.neusoft.elderlycare.servicemodule.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.CareService;
import com.neusoft.elderlycare.servicemodule.service.CareServiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/care-services")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
@Tag(name = "服务关注", description = "服务项目目录管理")
public class CareServiceController extends BaseCrudController<CareService> {
    private final CareServiceService careServiceService;

    @Override
    protected IService<CareService> service() {
        return careServiceService;
    }

    @Override
    protected QueryWrapper<CareService> wrapper(PageQuery query) {
        QueryWrapper<CareService> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.and(wrapper ->
                    wrapper.like("service_name", query.getKeyword())
                            .or()
                            .like("content", query.getKeyword())
            );
        }
        return w.orderByDesc("id");
    }
}
