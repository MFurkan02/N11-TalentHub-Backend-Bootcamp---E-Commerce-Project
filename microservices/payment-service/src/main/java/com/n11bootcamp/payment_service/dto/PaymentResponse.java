package com.n11bootcamp.payment_service.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private boolean success;
    private String message;
    private String transactionId;
}