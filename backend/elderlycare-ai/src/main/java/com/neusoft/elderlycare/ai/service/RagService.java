package com.neusoft.elderlycare.ai.service;

import com.neusoft.elderlycare.ai.rag.SimpleVectorStore;
import com.neusoft.elderlycare.ai.rag.SimpleVectorStore.VectorDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG（检索增强生成）服务
 *
 * 流程:
 * 1. 用户提问 → 向量化查询
 * 2. 从向量库检索相关文档块 (Retrieval)
 * 3. 将检索结果 + 用户问题 → 构造 Prompt
 * 4. 调用 LLM 生成回答 (Generation)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final LlmService llmService;
    private final SimpleVectorStore vectorStore;
    private final KnowledgeBaseService knowledgeBaseService;

    private static final String RAG_SYSTEM_PROMPT = """
            你是东软颐养中心的AI养老护理助手，专业亲切。根据参考资料简洁回答。
            ---参考资料开始---
            %s
            ---参考资料结束---
            """;

    /**
     * RAG 检索增强问答
     */
    public String query(String userQuery, int topK) {
        // 1. 知识库为空时直接走 LLM
        if (vectorStore.size() == 0) {
            log.info("RAG: 知识库为空，直接调用 LLM");
            return llmService.chat(
                    "你是东软颐养中心AI养老护理助手，简洁回答。",
                    userQuery
            );
        }

        // 2. 检索相关文档
        float[] queryEmbedding = llmService.embeddings(userQuery);
        List<VectorDocument> relevantDocs = vectorStore.search(queryEmbedding, topK);

        if (relevantDocs.isEmpty()) {
            log.info("RAG: 知识库无匹配文档，直接调用 LLM");
            return llmService.chat(
                    "你是东软颐养中心AI养老护理助手，简洁回答。",
                    userQuery
            );
        }

        // 3. 构造参考资料
        String referenceText = relevantDocs.stream()
                .map(doc -> String.format("[%s] %s", doc.getTitle(), doc.getContent()))
                .collect(Collectors.joining("\n\n"));

        // 4. 构造 Prompt 并调用 LLM
        String systemPrompt = RAG_SYSTEM_PROMPT.replace("%s", referenceText);
        log.info("RAG: 检索到 {} 个相关文档块，调用 LLM 生成回答", relevantDocs.size());

        return llmService.chat(systemPrompt, userQuery);
    }

    /**
     * 智能健康分析（基于知识库 + LLM）
     */
    public String healthAnalysis(String customerName, String healthInfo) {
        String query = String.format("请为老人 %s 进行健康评估分析。健康信息: %s", customerName, healthInfo);
        return query(query, 5);
    }

    /**
     * 护理方案推荐
     */
    public String careRecommendation(String customerName, String condition) {
        String query = String.format("请为老人 %s 推荐护理方案。当前状况: %s", customerName, condition);
        return query(query, 5);
    }

    /**
     * 统一处理 AI 分析事件（供 MQ Consumer 和 Spring EventListener 共用）
     */
    public String processAnalysisEvent(String customerName, String analysisType, String extraData) {
        return switch (analysisType) {
            case "HEALTH_ASSESSMENT" -> healthAnalysis(
                    customerName, extraData != null ? extraData : "暂无详细健康信息");
            case "CARE_RECOMMENDATION" -> careRecommendation(
                    customerName, extraData != null ? extraData : "暂无详细状况信息");
            default -> query("请对入住老人 " + customerName + " 进行综合评估", 3);
        };
    }
}
