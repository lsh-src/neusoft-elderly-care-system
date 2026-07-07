package com.neusoft.elderlycare.vo;

import com.neusoft.elderlycare.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginVO {
    @Schema(description = "JWT Token")
    private String token;
    @Schema(description = "用户信息")
    private SysUser user;
}
