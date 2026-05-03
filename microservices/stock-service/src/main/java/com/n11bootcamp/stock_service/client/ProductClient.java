package com.n11bootcamp.stock_service.client;

import com.n11bootcamp.stock_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/product")
    List<ProductDto> getAllProducts();
}