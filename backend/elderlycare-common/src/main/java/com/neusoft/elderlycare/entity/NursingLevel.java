package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nursing_level")
@Schema(description = "护理级别")
public class NursingLevel extends BaseEntity {
    @Schema(description = "级别名称", example = "一级护理")
    private String levelName;
    @Schema(description = "级别描述")
    private String description;
    @Schema(description = "收费标准", example = "3000.00")
    private BigDecimal fee;
}
