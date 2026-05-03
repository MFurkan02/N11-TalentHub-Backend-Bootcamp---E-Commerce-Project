package com.n11bootcamp.order_service.controller;

import com.n11bootcamp.order_service.dto.CreateOrderRequest;
import com.n11bootcamp.order_service.dto.OrderResponse;
import com.n11bootcamp.order_service.service.impl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Service", description = "Sipariş oluşturma, sorgulama ve kullanıcı sipariş geçmişi işlemlerini yönetir.")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;
    public OrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @Operation(
            summary = "Sipariş Tamamla (Checkout)",
            description = "Sepetteki ürünleri siparişe dönüştürür, stok düşümü ve ödeme kontrolü süreçlerini başlatır."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sipariş başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Stok yetersiz veya geçersiz sepet bilgisi"),
            @ApiResponse(responseCode = "402", description = "Ödeme başarısız")
    })
    @PostMapping("/checkout")
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderServiceImpl.createOrder(request);
    }

    @Operation(summary = "Tüm Siparişleri Getir", description = "Sistemdeki tüm siparişlerin listesini döner (Admin kullanımı için).")
    @GetMapping("/all")
    public List<OrderResponse> getAllOrders() {
        return orderServiceImpl.findAllOrders();
    }

    @Operation(summary = "ID ile Sipariş Sorgula", description = "Sipariş detaylarını ve durumunu ID üzerinden getirir.")
    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @Parameter(description = "Sipariş benzersiz ID'si", example = "5001")
            @PathVariable Long id) {
        return orderServiceImpl.getOrderById(id);
    }

    @Operation(summary = "Kullanıcı Sipariş Geçmişi", description = "Belirli bir kullanıcıya ait geçmiş tüm siparişleri listeler.")
    @GetMapping("/user/{username}")
    public List<OrderResponse> getOrdersByUser(
            @Parameter(description = "Sorgulanacak kullanıcı adı", example = "furkan123")
            @PathVariable String username) {
        return orderServiceImpl.findOrdersByUsername(username);
    }
}