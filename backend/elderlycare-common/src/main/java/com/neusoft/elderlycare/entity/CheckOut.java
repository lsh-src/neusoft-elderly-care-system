package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("check_out")
@Schema(description = "退住登记")
public class CheckOut extends BaseEntity {
    @Schema(description = "退住编号", example = "CO20260001")
    private String checkoutNo;
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    @Schema(description = "退住日期", example = "2026-07-15")
    private LocalDate checkoutDate;
    @Schema(description = "退住原因")
    private String reason;
    @Schema(description = "经办人", example = "王经理")
    private String operator;
    // ===================== 新增：客户姓名 =====================
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName;
}
