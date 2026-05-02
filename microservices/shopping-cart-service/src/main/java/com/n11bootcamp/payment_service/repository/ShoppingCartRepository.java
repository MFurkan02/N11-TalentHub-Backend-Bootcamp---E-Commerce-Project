package com.n11bootcamp.payment_service.repository;


import java.util.Optional;


import com.n11bootcamp.payment_service.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    //Optional<ShoppingCart> findById(Long id);

    Optional<ShoppingCart> findByShoppingCartName(String shoppingCartName);
}

