package com.n11bootcamp.shopping_cart_service.controller;

import com.n11bootcamp.shopping_cart_service.entity.CartItem;
import com.n11bootcamp.shopping_cart_service.entity.ShoppingCart;
import com.n11bootcamp.shopping_cart_service.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/shopping-cart")
@Tag(name = "Shopping Cart Service", description = "Sepet oluşturma, ürün ekleme/çıkarma ve fiyat hesaplama işlemlerini yönetir.")
public class ShoppingCartController  {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Operation(summary = "Yeni Sepet Oluştur", description = "Kullanıcı veya oturum bazlı yeni bir alışveriş sepeti oluşturur.")
    @PostMapping
    public ResponseEntity<ShoppingCart> createCart(@RequestParam("name") String name) {
        return shoppingCartService.createCart(name);
    }

    @Operation(summary = "Sepete Ürün Ekle", description = "Belirtilen sepet ID'sine bir veya birden fazla ürün (CartItem) ekler.")
    @PostMapping("{id}")
    public ResponseEntity<ShoppingCart> addProductsToCart(
            @PathVariable("id") Long shoppingCartId,
            @RequestBody List<CartItem> products) {
        return shoppingCartService.addProducts(shoppingCartId, products);
    }

    @Operation(summary = "Ürün Adedi Güncelle", description = "Sepetteki bir ürünün miktarını (adet) artırır veya azaltır.")
    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<ShoppingCart> updateAmount(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @RequestParam int amount
    ) {
        return shoppingCartService.updateAmount(cartId, productId, amount);
    }

    @Operation(summary = "Sepetten Ürün Çıkar", description = "Belirli bir ürünü sepetten tamamen kaldırır.")
    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<ShoppingCart> removeProduct(
            @PathVariable("id") Long shoppingCartId,
            @PathVariable("productId") Long productId) {
        return shoppingCartService.removeProduct(shoppingCartId, productId);
    }

    @Operation(summary = "Toplam Fiyat Hesapla", description = "Sepetteki tüm ürünlerin vergi ve indirimler dahil toplam tutarını döner.")
    @GetMapping("/totalprice/{id}")
    public ResponseEntity<Map<String, String>> getTotalPrice(
            @PathVariable("id") Long shoppingCartId) {
        return shoppingCartService.getShoppingCartPrice(shoppingCartId);
    }

    @Operation(summary = "ID ile Sepet Getir", description = "Sepet detaylarını getirir.")
    @GetMapping("{id}")
    public ResponseEntity<ShoppingCart> getCartById(
            @PathVariable("id") Long shoppingCartId
    ) {
        return shoppingCartService.getCartById(shoppingCartId);
    }

    @Operation(summary = "İsim ile Sepet Bul", description = "Kullanıcı ismine göre atanmış sepeti getirir.")
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ShoppingCart> getCartByShoppingCartName(
            @PathVariable("name") String shoppingCartName
    ) {
        return shoppingCartService.getCartByShoppingCartName(shoppingCartName);
    }

    @Operation(summary = "Tüm Sepetleri Listele", description = "Sistemdeki tüm sepetlerin listesini (i18n destekli) döner.")
    @GetMapping
    public ResponseEntity<List<ShoppingCart>> getAllCarts(
    ) {
        return shoppingCartService.getAllCarts();
    }

    @Operation(summary = "Sepeti Sil", description = "ID'si verilen sepeti sistemden tamamen temizler.")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCartById(@PathVariable("id") Long shoppingCartId) {
        return shoppingCartService.deleteCartById(shoppingCartId);
    }

    @Operation(
            summary = "Sepet İçeriğini Temizle",
            description = "İsim üzerinden ilgili sepeti bulur ve içindeki tüm ürünleri (CartItems) siler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sepet başarıyla boşaltıldı"),
            @ApiResponse(responseCode = "404", description = "Belirtilen isimde bir sepet bulunamadı")
    })
    @DeleteMapping("/clear/{name}")
    public ResponseEntity<String> clearCartByShoppingCartName(
            @Parameter(description = "Temizlenecek sepetin adı (Genelde kullanıcı adı)", example = "furkan123")
            @PathVariable("name") String shoppingCartName
    ) {
        return shoppingCartService.clearCartByShoppingCartName(shoppingCartName);
    }

    @Operation(summary = "Tüm Sepetleri Temizle", description = "Sistemdeki tüm alışveriş sepetlerini toplu olarak siler.")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllCarts() {
        return shoppingCartService.deleteAllCarts();
    }
}