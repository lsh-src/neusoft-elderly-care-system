package com.neusoft.elderlycare.mq;

/**
 * MQ 常量 — 不依赖 AMQP 库，任何模块均可引用
 */
public final class MqConstants {
    private MqConstants() {}

    // ==================== 交换机 ====================
    public static final String EXCHANGE_BUSINESS = "elderlycare.business.exchange";
    public static final String EXCHANGE_AI = "elderlycare.ai.exchange";

    // ==================== 队列 ====================
    public static final String QUEUE_CHECKIN = "elderlycare.queue.checkin";
    public static final String QUEUE_CHECKOUT = "elderlycare.queue.checkout";
    public static final String QUEUE_SERVICE_PURCHASE = "elderlycare.queue.service.purchase";
    public static final String QUEUE_AI_ANALYSIS = "elderlycare.queue.ai.analysis";
    public static final String QUEUE_AI_NOTIFICATION = "elderlycare.queue.ai.notification";

    // ==================== Routing Key ====================
    public static final String KEY_CHECKIN_CREATED = "checkin.created";
    public static final String KEY_CHECKOUT_CREATED = "checkout.created";
    public static final String KEY_SERVICE_PURCHASED = "service.purchased";
    public static final String KEY_AI_ANALYZE = "ai.analyze";
    public static final String KEY_AI_NOTIFY = "ai.notify";
}
