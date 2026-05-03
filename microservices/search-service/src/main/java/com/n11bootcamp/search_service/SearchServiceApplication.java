package com.n11bootcamp.search_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableDiscoveryClient
@CrossOrigin
public class SearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchServiceApplication.class, args);
	}

}
