package com.njucm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.njucm.item.mapper")
public class WpItemService {
    public static void main(String[] args) {
        SpringApplication.run(WpItemService.class, args);
    }
}
