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
@Schema(description = "RAG查询请求")
public class RagQueryRequest {
    @NotBlank(message = "查询不能为空")
    @Size(max = 5000, message = "查询长度不能超过5000字符")
    @Schema(description = "用户问题", required = true)
    private String query;
    @Schema(description = "检索Top-K数量", defaultValue = "3")
    private int topK = 3;
}
