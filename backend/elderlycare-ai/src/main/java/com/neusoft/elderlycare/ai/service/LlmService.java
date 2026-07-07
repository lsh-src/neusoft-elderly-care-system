package com.neusoft.elderlycare.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neusoft.elderlycare.ai.config.AiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 大语言模型调用服务
 * 支持 OpenAI 兼容 API（DeepSeek、通义千问、Ollama 等）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {

    private final AiConfig aiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 调用 LLM 进行对话
     */
    public String chat(String systemPrompt, String userMessage) {
        try {
            String url = aiConfig.getBaseUrl() + "/v1/chat/completions";

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", aiConfig.getModel());
            body.put("max_tokens", aiConfig.getMaxTokens());
            body.put("temperature", aiConfig.getTemperature());

            List<Map<String, String>> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                messages.add(Map.of("role", "system", "content", systemPrompt));
            }
            messages.add(Map.of("role", "user", "content", userMessage));
            body.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApiKey());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            JsonNode message = json.path("choices").path(0).path("message");

            // 优先取 content，若为空则取 reasoning_content（MiMo 推理模型兼容）
            String content = message.path("content").asText("");
            if (content.isBlank()) {
                content = message.path("reasoning_content").asText("暂无回复");
            }
            return content;
        } catch (Exception e) {
            log.error("LLM 调用失败: {}", e.getMessage());
            throw new RuntimeException("LLM 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * Embedding API 熔断状态：true=可用，false=降级中
     * 每5分钟自动重试一次真实API
     */
    private volatile boolean embeddingAvailable = true;
    private volatile long lastFailureTime = 0;
    private static final long RETRY_INTERVAL_MS = 5 * 60 * 1000L; // 5分钟

    /**
     * 生成文本向量嵌入
     */
    public float[] embeddings(String text) {
        // 降级状态下，每隔一段时间自动重试真实API
        if (!embeddingAvailable && System.currentTimeMillis() - lastFailureTime > RETRY_INTERVAL_MS) {
            embeddingAvailable = true;
            log.info("Embedding API 重试中...");
        }
        if (!embeddingAvailable) {
            return fallbackEmbedding(text);
        }
        try {
            String url = aiConfig.getBaseUrl() + "/v1/embeddings";

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", aiConfig.getEmbeddingModel());
            body.put("input", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApiKey());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            JsonNode embeddingArray = json.path("data").path(0).path("embedding");

            if (embeddingArray.isMissingNode() || !embeddingArray.isArray()) {
                // 如果 embedding API 不可用，使用简单哈希向量
                return fallbackEmbedding(text);
            }

            float[] vec = new float[embeddingArray.size()];
            for (int i = 0; i < embeddingArray.size(); i++) {
                vec[i] = (float) embeddingArray.get(i).asDouble();
            }
            return vec;
        } catch (Exception e) {
            log.warn("Embedding API 不可用，后续将使用降级方案: {}", e.getMessage());
            embeddingAvailable = false;
            lastFailureTime = System.currentTimeMillis();
            return fallbackEmbedding(text);
        }
    }

    /**
     * 降级向量方案 — 基于字符哈希的简单向量
     * 用于 embedding API 不可用时的 RAG 降级
     */
    private float[] fallbackEmbedding(String text) {
        int dim = 128;
        float[] vec = new float[dim];
        for (int i = 0; i < text.length(); i++) {
            vec[i % dim] += text.charAt(i) * 0.001f;
        }
        // 归一化
        float norm = 0;
        for (float v : vec) norm += v * v;
        norm = (float) Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < dim; i++) vec[i] /= norm;
        }
        return vec;
    }
}
