package com.neusoft.elderlycare.checkin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.neusoft.elderlycare.feign")
@ComponentScan(basePackages = {
    "com.neusoft.elderlycare",
    "com.neusoft.elderlycare.checkin",
    "com.neusoft.elderlycare.config",
    "com.neusoft.elderlycare.security",
    "com.neusoft.elderlycare.service",
    "com.neusoft.elderlycare.auth",
    "com.neusoft.elderlycare.config",
    "com.neusoft.elderlycare.security",
    "com.neusoft.elderlycare.service",
    "com.neusoft.elderlycare.checkin.controller",
    
    
    "com.neusoft.elderlycare.util"
})
@MapperScan({"com.neusoft.elderlycare.checkin.mapper", "com.neusoft.elderlycare.mapper"})
public class CheckInModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckInModuleApplication.class, args);
    }
}
