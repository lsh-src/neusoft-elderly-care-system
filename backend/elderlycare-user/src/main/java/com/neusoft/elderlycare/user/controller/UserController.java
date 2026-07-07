package com.neusoft.elderlycare.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.SysUser;
import com.neusoft.elderlycare.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "用户管理", description = "系统用户 CRUD（仅管理员）")
public class UserController extends BaseCrudController<SysUser> {
    private final SysUserService sysUserService;

    @Override
    protected IService<SysUser> service() {
        return sysUserService;
    }

    @Override
    protected QueryWrapper<SysUser> wrapper(PageQuery query) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

        // 1. 角色筛选
        if (StringUtils.hasText(query.getRole())) {
            wrapper.eq("role", query.getRole());
        }
        // 2. 账号状态筛选
        if (query.getEnabled() != null) {
            wrapper.eq("enabled", query.getEnabled());
        }
        // 3. 姓名模糊查询
        if (StringUtils.hasText(query.getName())) {
            wrapper.like("name", query.getName());
        }
        // 4. 手机号模糊查询
        if (StringUtils.hasText(query.getPhone())) {
            wrapper.like("phone", query.getPhone());
        }
        // 5. 全局关键词（姓名/手机号）
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like("name", query.getKeyword())
                    .or().like("phone", query.getKeyword()));
        }

        // 按ID倒序
        return wrapper.orderByDesc("id");
    }
}