package com.neusoft.elderlycare.checkin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.neusoft.elderlycare.feign")
@EnableRetry
@ComponentScan(basePackages = "com.neusoft.elderlycare")
@MapperScan({"com.neusoft.elderlycare.checkin.mapper", "com.neusoft.elderlycare.mapper"})
public class CheckInModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckInModuleApplication.class, args);
    }
}
