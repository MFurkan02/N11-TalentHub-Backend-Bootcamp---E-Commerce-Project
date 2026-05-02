package com.n11bootcamp.payment_service.repository;

import com.n11bootcamp.payment_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}