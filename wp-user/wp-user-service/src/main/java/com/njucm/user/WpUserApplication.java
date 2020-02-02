package com.njucm.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.njucm.user.mapper")
public class WpUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(WpUserApplication.class, args);
    }
}
