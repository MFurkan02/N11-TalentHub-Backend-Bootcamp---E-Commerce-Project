package com.n11bootcamp.payment_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaymentRequest {
    private Long orderId;
    private String username;
    private Double amount;
    private String paymentMethod;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String streetAddress;
    private String country;
    private Card card;
    private List<Item> items;

    @Data
    public static class Card {
        private String cardHolderName;
        private String cardNumber;
        private String expireMonth;
        private String expireYear;
        private String cvc;
    }

    @Data
    public static class Item {
        private Long productId;
        private String productName;
        private Double price;
        private Integer quantity;
        private String category1;
        private String category2;
    }
}