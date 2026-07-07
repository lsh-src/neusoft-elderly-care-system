package com.neusoft.elderlycare.bed;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
    "com.neusoft.elderlycare",
    "com.neusoft.elderlycare.bed",
    "com.neusoft.elderlycare.config",
    "com.neusoft.elderlycare.security",
    "com.neusoft.elderlycare.service",
    "com.neusoft.elderlycare.auth",
    "com.neusoft.elderlycare.config",
    "com.neusoft.elderlycare.security",
    "com.neusoft.elderlycare.service",
    "com.neusoft.elderlycare.bed.controller",
    
    
    "com.neusoft.elderlycare.util"
})
@MapperScan({"com.neusoft.elderlycare.bed.mapper", "com.neusoft.elderlycare.mapper"})
public class BedApplication {
    public static void main(String[] args) {
        SpringApplication.run(BedApplication.class, args);
    }
}
