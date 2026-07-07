package com.neusoft.elderlycare.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
    "com.neusoft.elderlycare",
    "com.neusoft.elderlycare.customer",
    "com.neusoft.elderlycare.config",
    "com.neusoft.elderlycare.security",
    "com.neusoft.elderlycare.service",
    "com.neusoft.elderlycare.auth",
    "com.neusoft.elderlycare.config",
    "com.neusoft.elderlycare.security",
    "com.neusoft.elderlycare.service",
    "com.neusoft.elderlycare.customer.controller",
    "com.neusoft.elderlycare.util"
})
@MapperScan({"com.neusoft.elderlycare.customer.mapper", "com.neusoft.elderlycare.mapper"})
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}
