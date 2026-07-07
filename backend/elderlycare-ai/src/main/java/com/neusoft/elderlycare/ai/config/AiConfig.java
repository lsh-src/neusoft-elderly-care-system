package com.neusoft.elderlycare.ai.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * AI 服务配置
 */
@Configuration
@Getter
@Setter
@ToString(exclude = "apiKey")
@ConfigurationProperties(prefix = "ai")
public class AiConfig {
    private String apiKey;
    private String baseUrl;
    private String model;
    private String embeddingModel;
    private int maxTokens = 2048;
    private double temperature = 0.7;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);  // 连接超时 10 秒
        factory.setReadTimeout(90000);     // 读取超时 90 秒（AI 响应较慢）
        return new RestTemplate(factory);
    }
}
