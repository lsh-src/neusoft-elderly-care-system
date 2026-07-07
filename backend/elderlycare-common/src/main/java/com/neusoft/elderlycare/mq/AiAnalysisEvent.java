package com.neusoft.elderlycare.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI 分析事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 客户ID */
    private Long customerId;
    /** 客户姓名 */
    private String customerName;
    /** 分析类型: HEALTH_ASSESSMENT, CARE_RECOMMENDATION, RISK_ALERT */
    private String analysisType;
    /** 额外参数 */
    private String extraData;

    public static AiAnalysisEvent of(Long customerId, String customerName, String analysisType) {
        return new AiAnalysisEvent(customerId, customerName, analysisType, null);
    }
}
