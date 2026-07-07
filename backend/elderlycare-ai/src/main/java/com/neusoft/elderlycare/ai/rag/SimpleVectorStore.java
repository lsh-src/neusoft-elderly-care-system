package com.neusoft.elderlycare.ai.rag;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易内存向量存储
 * 支持向量相似度检索（余弦相似度）
 */
@Component
@Slf4j
public class SimpleVectorStore {

    /** 文档块存储: id -> VectorDocument */
    private final Map<String, VectorDocument> store = new ConcurrentHashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(exclude = "embedding")
    public static class VectorDocument {
        private String id;
        private String content;
        private String title;
        private String category;
        private float[] embedding;
        private Map<String, String> metadata;
    }

    /**
     * 存储文档块
     */
    public void add(VectorDocument doc) {
        store.put(doc.getId(), doc);
        log.debug("向量存储: 添加文档 {} [{}]", doc.getId(), doc.getTitle());
    }

    /**
     * 批量存储
     */
    public void addAll(List<VectorDocument> docs) {
        for (VectorDocument doc : docs) {
            add(doc);
        }
    }

    /**
     * 向量相似度检索 — 返回 Top-K 最相关文档
     */
    public List<VectorDocument> search(float[] queryEmbedding, int topK) {
        if (store.isEmpty()) {
            return Collections.emptyList();
        }

        // 计算所有文档与查询的余弦相似度
        List<Map.Entry<String, Double>> scored = new ArrayList<>();
        for (VectorDocument doc : store.values()) {
            if (doc.getEmbedding() == null) continue;
            double similarity = cosineSimilarity(queryEmbedding, doc.getEmbedding());
            scored.add(Map.entry(doc.getId(), similarity));
        }

        // 按相似度降序排列，取 Top-K
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<VectorDocument> results = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, scored.size()); i++) {
            VectorDocument doc = store.get(scored.get(i).getKey());
            if (doc != null) {
                results.add(doc);
            }
        }
        return results;
    }

    /**
     * 获取存储大小
     */
    public int size() {
        return store.size();
    }

    /**
     * 清空存储
     */
    public void clear() {
        store.clear();
    }

    /**
     * 按标题删除文档（用于去重）
     */
    public void removeByTitle(String title) {
        store.entrySet().removeIf(entry -> title.equals(entry.getValue().getTitle()));
    }

    /**
     * 余弦相似度计算
     */
    private double cosineSimilarity(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length) return 0;
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        return denominator == 0 ? 0 : dotProduct / denominator;
    }
}
