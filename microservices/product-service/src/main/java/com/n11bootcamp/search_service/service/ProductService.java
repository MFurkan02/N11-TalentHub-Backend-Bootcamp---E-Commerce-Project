package com.n11bootcamp.search_service.service;

import com.n11bootcamp.search_service.entity.Product;
import com.n11bootcamp.search_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ResponseEntity<Product> getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in DB"));
        return ResponseEntity.ok(product);
    }

    public ResponseEntity<List<Product>> allProducts() {
        List<Product> productList = productRepository.findAll();
        return ResponseEntity.ok(productList);
    }

    public ResponseEntity<Product> createProduct(Product product) {
        return ResponseEntity.ok().body(productRepository.save(product));
    }

    public ResponseEntity<Product> updateProduct(Long productId, Product updatedProduct) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in DB"));

        product.setImg(updatedProduct.getImg());
        product.setPrice(updatedProduct.getPrice());
        product.setLabels(updatedProduct.getLabels());
        product.setBrand(updatedProduct.getBrand());
        product.setColor(updatedProduct.getColor());
        product.setCategoryKey(updatedProduct.getCategoryKey());

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    public ResponseEntity<String> deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully");
        } else {
            throw new RuntimeException("Product not found in DB");
        }
    }

    public ResponseEntity<String> deleteAllProducts() {
        productRepository.deleteAll();
        return ResponseEntity.ok("All products deleted successfully");
    }

    /*public Product uploadImage(Long id, MultipartFile file) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get("./images/products/", filename);
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        product.setImg(filename);
        return productRepository.save(product);
    }*/

    public Product uploadImage(Long id, MultipartFile file) {
        try {
            System.out.println("🚀 Upload started for productId: " + id);

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            System.out.println("✅ Product found: " + product.getId());

            // 🔥 absolute path (safe + debug)
            String uploadDir = Paths.get(System.getProperty("user.dir"), "images", "products")
                    .toAbsolutePath()
                    .toString();

            System.out.println("📁 Upload directory: " + uploadDir);

            // klasör yoksa oluştur
            Files.createDirectories(Paths.get(uploadDir));
            System.out.println("📂 Directory ensured");

            // filename safe + unique
            String originalName = file.getOriginalFilename();
            System.out.println("📄 Original filename: " + originalName);

            if (originalName == null || originalName.isEmpty()) {
                throw new RuntimeException("File name is invalid");
            }

            String filename = UUID.randomUUID() + "_" + originalName.replace(" ", "_");

            System.out.println("🆕 Generated filename: " + filename);

            Path filepath = Paths.get(uploadDir, filename);
            System.out.println("💾 Saving to: " + filepath.toAbsolutePath());

            // file write
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✅ File saved successfully");

            // verify file exists
            if (!Files.exists(filepath)) {
                throw new RuntimeException("File was not saved!");
            }

            System.out.println("🔍 File exists check OK");

            // DB save path
            String imagePath = "/images/products/" + filename;
            product.setImg(imagePath);

            System.out.println("🧠 DB image path set: " + imagePath);

            Product saved = productRepository.save(product);

            System.out.println("🎉 Product updated successfully");

            return saved;

        } catch (Exception e) {
            System.out.println("❌ UPLOAD ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
        }
    }

    public Page<Product> getPaged(int page, int size) {
        return productRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
    }

    @Transactional
    public void handleCategoryKeyChange(String oldKey, String newKey) {
        try {
            if (oldKey == null || newKey == null || oldKey.equalsIgnoreCase(newKey)) {
                log.debug("Category key update ignored (null or same): {} -> {}", oldKey, newKey);
                return;
            }
            int updatedCount = productRepository.updateCategoryKeyForProducts(oldKey, newKey);
            log.info("Updated {} products: categoryKey '{}' -> '{}'", updatedCount, oldKey, newKey);
        } catch (Exception ex) {
            log.error("Failed to update product categoryKeys for '{}' -> '{}': {}", oldKey, newKey, ex.getMessage(), ex);
        }
    }

}
