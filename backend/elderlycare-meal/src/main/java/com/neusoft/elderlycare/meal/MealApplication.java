package com.neusoft.elderlycare.meal;

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
    "com.neusoft.elderlycare.meal",
    "com.neusoft.elderlycare.config",
    "com.neusoft.elderlycare.security",
    "com.neusoft.elderlycare.service",
    "com.neusoft.elderlycare.util"
})
@MapperScan({"com.neusoft.elderlycare.meal.mapper", "com.neusoft.elderlycare.mapper"})
public class MealApplication {
    public static void main(String[] args) {
        SpringApplication.run(MealApplication.class, args);
    }
}
