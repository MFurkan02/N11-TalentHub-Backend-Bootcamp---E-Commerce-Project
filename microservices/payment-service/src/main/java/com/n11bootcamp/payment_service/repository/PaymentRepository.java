package com.n11bootcamp.payment_service.repository;

import com.n11bootcamp.payment_service.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentTransaction, Long> {
}