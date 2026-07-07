package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_purchase")
@Schema(description = "服务购买记录")
public class ServicePurchase extends BaseEntity {
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    @Schema(description = "服务项目ID", example = "1")
    private Long serviceId;
    @Schema(description = "购买日期", example = "2026-06-01")
    private LocalDate purchaseDate;
    @Schema(description = "状态", example = "生效中", allowableValues = {"生效中","已过期","已退订"})
    private String status;
    // 联查展示字段
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName;
    @Schema(description = "服务名称（联查字段）")
    @TableField(exist = false)
    private String serviceName;
}
