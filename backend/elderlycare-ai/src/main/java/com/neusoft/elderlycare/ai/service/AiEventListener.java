package com.neusoft.elderlycare.ai.service;

import com.neusoft.elderlycare.mq.AiAnalysisEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * AI 事件监听器
 * 监听 Spring ApplicationEvent，触发 AI 分析
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiEventListener {

    private final RagService ragService;

    @EventListener
    public void onAiAnalysis(AiAnalysisEvent event) {
        log.info("收到 AI 分析事件: 客户={}, 类型={}", event.getCustomerName(), event.getAnalysisType());
        try {
            String result = ragService.processAnalysisEvent(
                    event.getCustomerName(), event.getAnalysisType(), event.getExtraData());
            log.info("AI 分析完成: 客户={}, 结果长度={}", event.getCustomerName(), result.length());
        } catch (Exception e) {
            log.error("AI 分析失败: {}", e.getMessage(), e);
        }
    }
}
