package com.neusoft.elderlycare.ai.controller;

import com.neusoft.elderlycare.ai.dto.*;
import com.neusoft.elderlycare.ai.service.KnowledgeBaseService;
import com.neusoft.elderlycare.ai.service.LlmService;
import com.neusoft.elderlycare.ai.service.RagService;
import com.neusoft.elderlycare.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI智能服务", description = "AI对话、RAG检索、知识库管理")
public class AiController {

    private final LlmService llmService;
    private final RagService ragService;
    private final KnowledgeBaseService knowledgeBaseService;

    @Operation(summary = "AI对话", description = "与AI大模型进行对话")
    @PostMapping("/chat")
    public ApiResponse<String> chat(@Valid @RequestBody ChatRequest request) {
        String systemPrompt = "你是东软颐养中心的AI养老护理助手，专业回答养老护理、健康管理相关问题，语气亲切，回答简洁。";
        String result = llmService.chat(systemPrompt, request.getMessage());
        return ApiResponse.success(result);
    }

    @Operation(summary = "RAG检索问答", description = "基于知识库的检索增强生成问答")
    @PostMapping("/rag/query")
    public ApiResponse<String> ragQuery(@Valid @RequestBody RagQueryRequest request) {
        String result = ragService.query(request.getQuery(), request.getTopK());
        return ApiResponse.success(result);
    }

    @Operation(summary = "上传知识文档", description = "将文档上传到知识库，自动分块向量化")
    @PostMapping("/knowledge/upload")
    public ApiResponse<Map<String, Object>> uploadKnowledge(@Valid @RequestBody KnowledgeUploadRequest request) {
        int chunks = knowledgeBaseService.uploadDocument(
                request.getTitle(), request.getContent(), request.getCategory());
        return ApiResponse.success(Map.of(
                "title", request.getTitle(),
                "chunks", chunks,
                "message", "文档已成功上传并索引"
        ));
    }

    @Operation(summary = "知识库统计", description = "获取知识库文档统计信息")
    @GetMapping("/knowledge/stats")
    public ApiResponse<Map<String, Object>> knowledgeStats() {
        return ApiResponse.success(knowledgeBaseService.stats());
    }

    @Operation(summary = "健康评估", description = "基于AI对老人进行健康评估分析")
    @PostMapping("/analyze/health")
    public ApiResponse<String> healthAnalysis(@Valid @RequestBody HealthAnalysisRequest request) {
        String result = ragService.healthAnalysis(
                request.getCustomerName(),
                request.getHealthInfo() != null ? request.getHealthInfo() : "暂无健康信息"
        );
        return ApiResponse.success(result);
    }

    @Operation(summary = "护理方案推荐", description = "AI推荐个性化护理方案")
    @PostMapping("/analyze/care")
    public ApiResponse<String> careRecommendation(@Valid @RequestBody CareRecommendationRequest request) {
        String result = ragService.careRecommendation(
                request.getCustomerName(),
                request.getCondition() != null ? request.getCondition() : "暂无状况信息"
        );
        return ApiResponse.success(result);
    }
}
