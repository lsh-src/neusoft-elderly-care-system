package com.neusoft.elderlycare.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.SysUser;
import com.neusoft.elderlycare.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "用户管理", description = "系统用户 CRUD（仅管理员）")
public class UserController extends BaseCrudController<SysUser> {
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected IService<SysUser> service() {
        return sysUserService;
    }

    /**
     * 重写新增：密码自动 BCrypt 加密
     */
    @Override
    @Operation(summary = "新增用户", description = "新增用户，密码自动加密存储")
    public ApiResponse<Boolean> create(@RequestBody SysUser body) {
        if (StringUtils.hasText(body.getPassword())) {
            body.setPassword(passwordEncoder.encode(body.getPassword()));
        }
        return ApiResponse.success(service().save(body));
    }

    /**
     * 重写更新：如果传了密码则加密，没传则保留原密码
     */
    @Override
    @Operation(summary = "修改用户", description = "修改用户信息，密码字段有值时自动加密")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody SysUser body) {
        body.setId(id);
        // 如果密码为空，不更新密码字段
        if (!StringUtils.hasText(body.getPassword())) {
            body.setPassword(null);
        } else {
            body.setPassword(passwordEncoder.encode(body.getPassword()));
        }
        return ApiResponse.success(service().updateById(body));
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