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
@Schema(description = "护理方案推荐请求")
public class CareRecommendationRequest {
    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100字符")
    @Schema(description = "客户姓名", required = true)
    private String customerName;

    @Size(max = 5000, message = "状况描述长度不能超过5000字符")
    @Schema(description = "当前状况")
    private String condition;
}
