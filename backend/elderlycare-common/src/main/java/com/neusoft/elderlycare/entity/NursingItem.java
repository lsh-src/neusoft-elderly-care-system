package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nursing_item")
@Schema(description = "护理项目")
public class NursingItem extends BaseEntity {
    @Schema(description = "项目名称", example = "血压监测")
    private String itemName;
    @Schema(description = "项目描述")
    private String description;
    @Schema(description = "护理级别ID", example = "1")
    private Long levelId;
    @Schema(description = "执行频率", example = "每日两次")
    private String frequency;
    // 非数据库字段：护理级别名称（联查展示用）
    @Schema(description = "护理级别名称（联查字段）")
    @TableField(exist = false)
    private String levelName;
}
