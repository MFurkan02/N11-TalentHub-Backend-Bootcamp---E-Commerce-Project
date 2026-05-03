package com.n11bootcamp.product_service.controller;



import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/product")
@Tag(name = "Product Service", description = "Ürün kataloğu, stok yönetimi ve ürün görseli işlemlerini yönetir.")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Yeni Ürün Oluştur", description = "Sisteme yeni bir ürün ekler.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @Operation(summary = "Ürün Güncelle", description = "Mevcut bir ürünün bilgilerini (ad, fiyat, açıklama vb.) ID üzerinden günceller.")
    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long productId,
                                                 @RequestBody Product updatedProduct) {
        return productService.updateProduct(productId, updatedProduct);
    }

    @Operation(summary = "Ürün Sil", description = "ID'si verilen ürünü sistemden kalıcı olarak kaldırır.")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

    @Operation(summary = "Tüm Ürünleri Sil", description = "Katalogdaki tüm ürünleri toplu olarak siler. (Dikkatli kullanın!)")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll() {
        return productService.deleteAllProducts();
    }

    @Operation(summary = "Tüm Ürünleri Listele", description = "Sayfalama olmaksızın tüm ürün listesini döner.")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return productService.allProducts();
    }

    @Operation(summary = "Sayfalı Ürün Listesi", description = "Frontend (React) pagination yapısı için özelleştirilmiş, meta veriler içeren ürün listesi.")
    @GetMapping("/paged")
    public ResponseEntity<?> getPaged(
            @Parameter(description = "Sayfa numarası (0'dan başlar)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına öğe sayısı", example = "10")
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

    @Operation(summary = "Detaylı Ürün Sorgulama", description = "Ürün detaylarını getirirken analiz için Header bilgilerini de loglar.")
    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable("id") Long productId,
            @Parameter(description = "İsteği atan kullanıcı ID'si") @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "Platform bilgisi", example = "WEB") @RequestHeader(value = "X-Platform", required = false, defaultValue = "WEB") String platform,
            @Parameter(description = "Kaynak bilgisi", example = "REACT") @RequestHeader(value = "X-Source", required = false, defaultValue = "REACT") String source,
            @RequestHeader(value = "X-Session-Id", required = false, defaultValue = "unknown") String sessionId
    ) {
        return productService.getProductById(productId);
    }

    @Operation(summary = "Ürün Görseli Yükle", description = "Ürün için yerel sunucuya veya buluta bir görsel dosyası yükler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görsel başarıyla yüklendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz dosya formatı")
    })
    @PostMapping(value = "/upload-image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> uploadImage(
            @PathVariable Long id,
            @Parameter(description = "Yüklenecek görsel dosyası (JPG/PNG)")
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(productService.uploadImage(id, file));
    }
}