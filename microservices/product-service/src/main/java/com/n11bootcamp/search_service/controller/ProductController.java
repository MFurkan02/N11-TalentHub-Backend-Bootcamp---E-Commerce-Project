package com.n11bootcamp.search_service.controller;



import com.n11bootcamp.search_service.entity.Product;
import com.n11bootcamp.search_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")

public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long productId,
                                                 @RequestBody Product updatedProduct) {
        return productService.updateProduct(productId, updatedProduct);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll() {
        return productService.deleteAllProducts();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return productService.allProducts();
    }


    @GetMapping("/paged")
    public ResponseEntity<?> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Page<Product> p = productService.getPaged(page, size);
        return ResponseEntity.ok(Map.of(
                "items", p.getContent(),
                "page", p.getNumber(),
                "size", p.getSize(),
                "totalElements", p.getTotalElements(),
                "totalPages", p.getTotalPages(),
                "isLast", p.isLast()
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable("id") Long productId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Platform", required = false, defaultValue = "WEB") String platform,
            @RequestHeader(value = "X-Source", required = false, defaultValue = "REACT") String source,
            @RequestHeader(value = "X-Session-Id", required = false, defaultValue = "unknown") String sessionId
    ) {
        ResponseEntity<Product> response = productService.getProductById(productId);
        return response;
    }

    @PostMapping(value = "/upload-image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(productService.uploadImage(id, file));
    }
}