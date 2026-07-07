package com.neusoft.elderlycare.ai.service;

import com.neusoft.elderlycare.ai.rag.SimpleVectorStore;
import com.neusoft.elderlycare.ai.rag.SimpleVectorStore.VectorDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 知识库管理服务
 * 负责文档的分块、向量化、存储
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseService {

    private final LlmService llmService;
    private final SimpleVectorStore vectorStore;
    private final AtomicLong docIdCounter = new AtomicLong(1);

    /** 文档块大小（字符数） */
    private static final int CHUNK_SIZE = 500;
    /** 文档块重叠（字符数） */
    private static final int CHUNK_OVERLAP = 50;

    /**
     * 上传文档到知识库
     * 自动分块 → 向量化 → 存储
     * 先删除同名旧文档，再重新索引，避免重复
     */
    public int uploadDocument(String title, String content, String category) {
        // 去重：删除同名旧文档
        vectorStore.removeByTitle(title);

        List<String> chunks = splitText(content, CHUNK_SIZE, CHUNK_OVERLAP);
        int count = 0;

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            String docId = "doc_" + docIdCounter.getAndIncrement();

            // 生成向量（带间隔限流，避免触发 API 限流）
            if (i > 0) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
            float[] embedding = llmService.embeddings(chunk);

            VectorDocument doc = new VectorDocument();
            doc.setId(docId);
            doc.setContent(chunk);
            doc.setTitle(title);
            doc.setCategory(category != null ? category : "other");
            doc.setEmbedding(embedding);
            doc.setMetadata(Map.of(
                    "title", title,
                    "chunkIndex", String.valueOf(i),
                    "totalChunks", String.valueOf(chunks.size())
            ));

            vectorStore.add(doc);
            count++;
        }

        log.info("知识库: 文档 [{}] 已分块为 {} 个片段并存入向量库", title, count);
        return count;
    }

    /**
     * 获取知识库统计
     */
    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalDocuments", vectorStore.size());
        stats.put("storeType", "In-Memory SimpleVectorStore");
        return stats;
    }

    /**
     * 文本分块（滑动窗口）
     */
    private List<String> splitText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) return chunks;

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            start += chunkSize - overlap;
        }
        return chunks;
    }
}
