package com.n11bootcamp.stock_service.service;


import com.n11bootcamp.stock_service.client.ProductClient;
import com.n11bootcamp.stock_service.dto.ProductDto;
import com.n11bootcamp.stock_service.entity.ProductStock;
import com.n11bootcamp.stock_service.repository.ProductStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockInitDataRunner implements CommandLineRunner {

    private final ProductStockRepository repo;
    private final ProductClient productClient;

    public StockInitDataRunner(ProductStockRepository repo, ProductClient productClient) {
        this.repo = repo;
        this.productClient = productClient;
    }

    @Override
    public void run(String... args) {
        try {
            // Product Service'ten tüm ürünleri çek
            List<ProductDto> products = productClient.getAllProducts();

            for (ProductDto product : products) {
                // Eğer bu ürün için stok kaydı yoksa oluştur
                if (product.getTitle() == null) {
                    System.out.println("⚠️ Uyarı: " + product.getId() + " ID'li ürünün ismi boş geldi, atlanıyor.");
                    continue; // İsmi null olan ürünü kaydetmeye çalışma
                }


                if (!repo.existsByProductId(product.getId())) {
                    // Başlangıç stoğu olarak rastgele veya sabit bir değer (örn: 50) veriyoruz
                    repo.save(new ProductStock(
                            product.getId(),
                            product.getTitle(),
                            50
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Product Service'e ulaşılamadı, stoklar initialize edilemedi: " + e.getMessage());
        }
    }
}

