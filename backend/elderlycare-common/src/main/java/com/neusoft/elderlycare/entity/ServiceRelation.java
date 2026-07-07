package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_relation")
@Schema(description = "服务关系")
public class ServiceRelation extends BaseEntity {
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    @Schema(description = "健康管家ID", example = "2")
    private Long managerId;
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName;

    @Schema(description = "健康管家姓名（联查字段）")
    @TableField(exist = false)
    private String managerName;
}
