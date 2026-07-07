package com.neusoft.elderlycare.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(
        basePackages = {"com.neusoft.elderlycare.ai", "com.neusoft.elderlycare.config"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.neusoft\\.elderlycare\\.config\\.Security.*")
)
public class AiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }
}
