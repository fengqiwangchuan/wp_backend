package com.njucm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class WpRegistry {
    public static void main(String[] args) {
        SpringApplication.run(WpRegistry.class, args);
    }
}
