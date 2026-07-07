package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("check_in")
@Schema(description = "入住登记")
public class CheckIn extends BaseEntity {
    @Schema(description = "登记编号", example = "CI20260001")
    private String registerNo;
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    @Schema(description = "床位ID", example = "1")
    private Long bedId;
    @Schema(description = "入住日期", example = "2026-01-15")
    private LocalDate checkInDate;
    @Schema(description = "合同月数", example = "6")
    private Integer contractMonths;
    @Schema(description = "押金", example = "5000.00")
    private BigDecimal deposit;
    @Schema(description = "经办人", example = "王经理")
    private String operator;
    // 联查非数据库字段
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName; // 客户姓名
    @Schema(description = "床位号（联查字段）")
    @TableField(exist = false)
    private String bedNo;       // 床位号
}
