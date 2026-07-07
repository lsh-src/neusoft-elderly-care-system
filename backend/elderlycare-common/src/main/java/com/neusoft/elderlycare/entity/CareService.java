package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("care_service")
@Schema(description = "服务项目")
public class CareService extends BaseEntity {
    @Schema(description = "服务名称", example = "日常护理")
    private String serviceName;
    @Schema(description = "价格", example = "200.00")
    private BigDecimal price;
    @Schema(description = "服务内容")
    private String content;
    @Schema(description = "服务周期", example = "每月")
    private String period;
}
