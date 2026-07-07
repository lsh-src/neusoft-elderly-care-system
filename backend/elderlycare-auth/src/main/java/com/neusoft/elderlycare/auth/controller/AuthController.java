package com.neusoft.elderlycare.auth.controller;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.dto.LoginDTO;
import com.neusoft.elderlycare.dto.PasswordDTO;
import com.neusoft.elderlycare.dto.RegisterDTO;
import com.neusoft.elderlycare.entity.SysUser;
import com.neusoft.elderlycare.service.SysUserService;
import com.neusoft.elderlycare.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、注册、获取当前用户")
public class AuthController {
    private final SysUserService userService;

    @Operation(summary = "用户登录", description = "手机号+密码登录，返回JWT Token")
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return ApiResponse.success(userService.login(dto));
    }

    @Operation(summary = "用户注册", description = "注册新用户账号")
    @PostMapping("/register")
    public ApiResponse<SysUser> register(@Valid @RequestBody RegisterDTO dto) {
        return ApiResponse.success(userService.register(dto));
    }

    @Operation(summary = "获取当前用户", description = "根据Token获取当前登录用户信息")
    @GetMapping("/me")
    public ApiResponse<SysUser> me() {
        return ApiResponse.success(userService.currentUser());
    }

    @Operation(summary = "修改密码", description = "当前登录用户修改密码")
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody PasswordDTO dto) {
        userService.changePassword(dto);
        return ApiResponse.success();
    }
}
