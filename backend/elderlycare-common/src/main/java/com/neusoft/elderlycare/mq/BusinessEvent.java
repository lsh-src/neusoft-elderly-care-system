package com.neusoft.elderlycare.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 业务事件消息体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessEvent<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 事件类型: CHECKIN_CREATED, CHECKOUT_CREATED, SERVICE_PURCHASED */
    private String eventType;
    /** 事件数据 */
    private T data;
    /** 事件时间 */
    private LocalDateTime eventTime;
    /** 来源服务 */
    private String source;

    public static <T> BusinessEvent<T> of(String eventType, T data, String source) {
        return new BusinessEvent<>(eventType, data, LocalDateTime.now(), source);
    }
}
