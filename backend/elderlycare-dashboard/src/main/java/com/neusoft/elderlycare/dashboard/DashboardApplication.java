package com.neusoft.elderlycare.dashboard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.neusoft.elderlycare.feign")
@ComponentScan(basePackages = "com.neusoft.elderlycare")
@MapperScan({"com.neusoft.elderlycare.dashboard.mapper", "com.neusoft.elderlycare.mapper"})
public class DashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }
}
