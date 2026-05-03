package com.n11bootcamp.shopping_cart_service.entity;

import java.util.Set;

import jakarta.persistence.*;

@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String shoppingCartName;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items;

    // -------------------- GETTER & SETTER --------------------

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getShoppingCartName() {
        return shoppingCartName;
    }
    public void setShoppingCartName(String shoppingCartName) {
        this.shoppingCartName = shoppingCartName;
    }

    public Set<CartItem> getItems() {
        return items;
    }
    public void setItems(Set<CartItem> items) {
        this.items = items;
    }
}