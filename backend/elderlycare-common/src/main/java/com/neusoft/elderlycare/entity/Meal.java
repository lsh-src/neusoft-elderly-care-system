package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("meal")
@Schema(description = "膳食信息")
public class Meal extends BaseEntity {
    @Schema(description = "膳食编号", example = "M20260001")
    private String mealNo;
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    @Schema(description = "早餐")
    private String breakfast;
    @Schema(description = "午餐")
    private String lunch;
    @Schema(description = "晚餐")
    private String dinner;
    @Schema(description = "特殊需求")
    private String specialNeed;
    @Schema(description = "膳食日期", example = "2026-06-09")
    private LocalDate mealDate;
    @Schema(description = "膳食图片URL")
    private String mealImg;

    // 非数据库字段：客户姓名
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName;
}