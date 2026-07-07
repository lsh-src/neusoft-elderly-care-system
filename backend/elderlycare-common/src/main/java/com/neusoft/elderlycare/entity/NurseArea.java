package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nurse_area")
@Schema(description = "护理区域")
public class NurseArea extends BaseEntity {
    @Schema(description = "区域名称", example = "A区")
    private String areaName;
    @Schema(description = "备注")
    private String remark;
}
