package com.neusoft.elderlycare.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "注册请求参数")
public class RegisterDTO {
    @Schema(description = "手机号", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String phone;
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;
    @Schema(description = "姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;
    @Schema(description = "年龄", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer age;
    @Schema(description = "性别", example = "男", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String gender;
}
