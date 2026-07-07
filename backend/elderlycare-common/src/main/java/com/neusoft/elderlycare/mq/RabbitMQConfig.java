package com.neusoft.elderlycare.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置 — 仅在 AMQP 库可用时生效
 */
@Configuration
@ConditionalOnClass(DirectExchange.class)
public class RabbitMQConfig {

    @Bean
    public DirectExchange businessExchange() {
        return new DirectExchange(MqConstants.EXCHANGE_BUSINESS);
    }

    @Bean
    public DirectExchange aiExchange() {
        return new DirectExchange(MqConstants.EXCHANGE_AI);
    }

    @Bean
    public Queue checkinQueue() {
        return QueueBuilder.durable(MqConstants.QUEUE_CHECKIN).build();
    }

    @Bean
    public Queue checkoutQueue() {
        return QueueBuilder.durable(MqConstants.QUEUE_CHECKOUT).build();
    }

    @Bean
    public Queue servicePurchaseQueue() {
        return QueueBuilder.durable(MqConstants.QUEUE_SERVICE_PURCHASE).build();
    }

    @Bean
    public Queue aiAnalysisQueue() {
        return QueueBuilder.durable(MqConstants.QUEUE_AI_ANALYSIS).build();
    }

    @Bean
    public Queue aiNotificationQueue() {
        return QueueBuilder.durable(MqConstants.QUEUE_AI_NOTIFICATION).build();
    }

    @Bean
    public Binding checkinBinding() {
        return BindingBuilder.bind(checkinQueue()).to(businessExchange()).with(MqConstants.KEY_CHECKIN_CREATED);
    }

    @Bean
    public Binding checkoutBinding() {
        return BindingBuilder.bind(checkoutQueue()).to(businessExchange()).with(MqConstants.KEY_CHECKOUT_CREATED);
    }

    @Bean
    public Binding servicePurchaseBinding() {
        return BindingBuilder.bind(servicePurchaseQueue()).to(businessExchange()).with(MqConstants.KEY_SERVICE_PURCHASED);
    }

    @Bean
    public Binding aiAnalysisBinding() {
        return BindingBuilder.bind(aiAnalysisQueue()).to(aiExchange()).with(MqConstants.KEY_AI_ANALYZE);
    }

    @Bean
    public Binding aiNotificationBinding() {
        return BindingBuilder.bind(aiNotificationQueue()).to(aiExchange()).with(MqConstants.KEY_AI_NOTIFY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
