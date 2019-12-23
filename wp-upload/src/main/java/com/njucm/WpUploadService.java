package com.njucm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WpUploadService {
    public static void main(String[] args) {
        SpringApplication.run(WpUploadService.class, args);
    }
}
