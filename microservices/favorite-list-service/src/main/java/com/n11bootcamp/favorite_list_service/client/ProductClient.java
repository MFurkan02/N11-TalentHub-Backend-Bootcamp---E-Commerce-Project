package com.n11bootcamp.favorite_list_service.client;

import com.n11bootcamp.favorite_list_service.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// "product-service" ismi, Product servisinin application.properties içindeki spring.application.name değeridir.
@FeignClient(name = "product-service")
public interface ProductClient {

    // Product servisindeki hangi endpoint'e gideceğini belirtiyorsun
    @GetMapping("/api/product/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
}