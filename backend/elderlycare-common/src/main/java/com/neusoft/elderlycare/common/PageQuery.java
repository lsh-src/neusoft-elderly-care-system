package com.neusoft.elderlycare.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询参数")
public class PageQuery {
    private static final long MAX_SIZE = 100;

    // 基础分页
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;
    @Schema(description = "每页条数", example = "10")
    private Long size = 10L;
    // 通用关键词
    @Schema(description = "通用关键词搜索")
    private String keyword;

    // 用户多条件查询字段
    @Schema(description = "角色筛选", example = "ADMIN")
    private String role;
    @Schema(description = "姓名筛选")
    private String name;
    @Schema(description = "手机号筛选")
    private String phone;
    @Schema(description = "账号状态筛选", example = "1")
    private Integer enabled;

    public Long getSize() {
        return (size == null || size > MAX_SIZE) ? MAX_SIZE : Math.max(size, 1);
    }

    public Long getCurrent() {
        return (current == null || current < 1) ? 1L : current;
    }
}