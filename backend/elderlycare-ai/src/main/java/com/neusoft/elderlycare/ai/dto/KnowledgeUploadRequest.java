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
@Schema(description = "知识库文档上传请求")
public class KnowledgeUploadRequest {
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    @Schema(description = "文档标题", required = true)
    private String title;
    @NotBlank(message = "内容不能为空")
    @Size(max = 1000000, message = "内容长度不能超过1MB")
    @Schema(description = "文档内容", required = true)
    private String content;
    @Schema(description = "文档分类: health(健康), service(服务), policy(政策), other(其他)")
    private String category;
}
