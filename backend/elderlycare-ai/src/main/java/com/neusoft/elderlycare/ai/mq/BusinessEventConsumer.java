package com.neusoft.elderlycare.ai.mq;

import com.neusoft.elderlycare.ai.service.RagService;
import com.neusoft.elderlycare.mq.AiAnalysisEvent;
import com.neusoft.elderlycare.mq.BusinessEvent;
import com.neusoft.elderlycare.mq.MqConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 消费者 — 监听业务事件并触发 AI 分析
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BusinessEventConsumer {

    private final RagService ragService;

    /**
     * 监听 AI 分析事件
     */
    @RabbitListener(queues = MqConstants.QUEUE_AI_ANALYSIS)
    public void onAiAnalysis(AiAnalysisEvent event) {
        log.info("[MQ] 收到 AI 分析事件: 客户={}, 类型={}", event.getCustomerName(), event.getAnalysisType());
        try {
            String result = ragService.processAnalysisEvent(
                    event.getCustomerName(), event.getAnalysisType(), event.getExtraData());
            log.info("[MQ] AI 分析完成: 客户={}, 结果长度={}", event.getCustomerName(), result.length());
        } catch (Exception e) {
            log.error("[MQ] AI 分析失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 监听入住业务事件
     */
    @RabbitListener(queues = MqConstants.QUEUE_CHECKIN)
    public void onCheckinCreated(BusinessEvent<?> event) {
        log.info("[MQ] 收到入住事件: 类型={}, 来源={}", event.getEventType(), event.getSource());
        // 入住事件已通过 AiAnalysisEvent 触发 AI 分析，此处仅记录日志
    }

    /**
     * 监听服务购买业务事件
     */
    @RabbitListener(queues = MqConstants.QUEUE_SERVICE_PURCHASE)
    public void onServicePurchased(BusinessEvent<?> event) {
        log.info("[MQ] 收到服务购买事件: 类型={}, 来源={}", event.getEventType(), event.getSource());
    }
}
