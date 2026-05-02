package com.n11bootcamp.favorite_list_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FavoriteListServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FavoriteListServiceApplication.class, args);
    }
}
