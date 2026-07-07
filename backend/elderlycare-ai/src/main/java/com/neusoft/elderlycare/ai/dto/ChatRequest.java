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
@Schema(description = "AI对话请求")
public class ChatRequest {
    @NotBlank(message = "消息不能为空")
    @Size(max = 5000, message = "消息长度不能超过5000字符")
    @Schema(description = "用户消息", required = true)
    private String message;
    @Schema(description = "对话上下文ID（可选，用于多轮对话）")
    private String conversationId;
}
