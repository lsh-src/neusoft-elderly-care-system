package com.neusoft.elderlycare.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康评估请求")
public class HealthAnalysisRequest {
    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100字符")
    @Schema(description = "客户姓名", required = true)
    private String customerName;

    @Size(max = 5000, message = "健康信息长度不能超过5000字符")
    @Schema(description = "健康信息")
    private String healthInfo;
}
