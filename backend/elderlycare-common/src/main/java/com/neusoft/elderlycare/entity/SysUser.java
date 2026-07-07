package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "系统用户")
public class SysUser extends BaseEntity {
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    @Schema(description = "密码", example = "123456")
    @ToString.Exclude
    private String password;
    @Schema(description = "姓名", example = "张三")
    private String name;
    @Schema(description = "年龄", example = "25")
    private Integer age;
    @Schema(description = "性别", example = "男")
    private String gender;
    @Schema(description = "角色", example = "ADMIN", allowableValues = {"ADMIN","MANAGER","USER"})
    private String role;
    @Schema(description = "账号状态", example = "1", allowableValues = {"0","1"})
    private Integer enabled;
}
