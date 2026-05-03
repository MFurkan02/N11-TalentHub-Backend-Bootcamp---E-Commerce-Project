package com.n11bootcamp.payment_service.controller;

import com.n11bootcamp.payment_service.dto.PaymentRequest;
import com.n11bootcamp.payment_service.dto.PaymentResponse;
import com.n11bootcamp.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Service", description = "Ödeme işlemlerini, kart doğrulama ve işlem geçmişini yönetir.")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Ödeme Yap",
            description = "Kredi kartı bilgilerini ve sepet tutarını alarak Iyzico veya simüle edilmiş sanal pos üzerinden ödeme işlemini gerçekleştirir."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ödeme başarıyla onaylandı",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Geçersiz kart bilgileri veya eksik parametre"),
            @ApiResponse(responseCode = "402", description = "Bakiye yetersiz veya banka tarafından reddedildi"),
            @ApiResponse(responseCode = "500", description = "Ödeme ağ geçidi (Gateway) hatası")
    })
    @PostMapping("/pay")
    public PaymentResponse makePayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}