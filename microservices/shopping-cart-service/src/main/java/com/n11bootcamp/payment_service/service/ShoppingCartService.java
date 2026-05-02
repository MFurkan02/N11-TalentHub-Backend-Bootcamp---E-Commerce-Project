package com.n11bootcamp.payment_service.service;


import java.util.*;


import com.n11bootcamp.payment_service.entity.CartItem;
import com.n11bootcamp.payment_service.entity.ShoppingCart;
import com.n11bootcamp.payment_service.repository.CartItemRepository;
import com.n11bootcamp.payment_service.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RestTemplate restTemplate;

    // ✅ Microservice discovery kullanıyorsan:
    private static final String PRODUCT_SERVICE = "http://PRODUCT-SERVICE";
    // ✅ Local test için istersen bunu açıp kapatabilirsin:
    // private static final String PRODUCT_SERVICE_BASE = "http://localhost:8764";

    public ResponseEntity<ShoppingCart> createCart(String name) {

        Optional<ShoppingCart> existing =
                shoppingCartRepository.findByShoppingCartName(name);

        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setShoppingCartName(name);

        return ResponseEntity.ok(shoppingCartRepository.save(cart));
    }

    public ResponseEntity<ShoppingCart> addProducts(Long cartId, List<CartItem> incomingItems) {

        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        for (CartItem incoming : incomingItems) {

            if (incoming == null || incoming.getProductId() == null) continue;

            Long productId = incoming.getProductId();

            Map product = restTemplate.getForObject(
                    PRODUCT_SERVICE + "/api/product/" + productId,
                    Map.class
            );

            if (product == null) continue;

            int price = ((Number) product.get("price")).intValue();
            String title = (String) product.get("title");
            String img = (String) product.get("img");

            // 🔥 mevcut item var mı
            CartItem existing = cart.getItems()
                    .stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setAmount(existing.getAmount() + incoming.getAmount());
            } else {
                CartItem item = new CartItem();
                item.setProductId(productId);
                item.setAmount(incoming.getAmount() <= 0 ? 1 : incoming.getAmount());
                item.setName(title);
                item.setPrice(price);
                item.setImage(img);
                item.setShoppingCart(cart);

                cart.getItems().add(item);
            }
        }

        ShoppingCart saved = shoppingCartRepository.save(cart);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<ShoppingCart> updateAmount(Long cartId, Long productId, int amount) {

        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found!"));

        CartItem targetItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            throw new RuntimeException("Product not found in cart!");
        }

        if (amount <= 0) {
            cart.getItems().remove(targetItem);
        } else {
            targetItem.setAmount(amount);
        }

        return ResponseEntity.ok(shoppingCartRepository.save(cart));
    }


    public ResponseEntity<ShoppingCart> removeProduct(Long cartId, Long productId) {

        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Set<CartItem> items = cart.getItems();

        if (items != null) {
            items.removeIf(i -> i.getProductId().equals(productId));
        }

        cart.setItems(items);
        return ResponseEntity.ok(shoppingCartRepository.save(cart));
    }



    // ---------------- TOTAL PRICE (REAL-TIME) ----------------
    public ResponseEntity<Map<String, String>> getShoppingCartPrice(Long cartId) {

        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        int total = 0;

        if (cart.getItems() != null) {

            for (CartItem item : cart.getItems()) {

                // 🔥 HER SEFERİNDE PRODUCT SERVICE'DEN EN GÜNCEL FİYAT
                Map product = restTemplate.getForObject(
                        PRODUCT_SERVICE + "/api/product/" + item.getProductId(),
                        Map.class
                );

                if (product == null) continue;

                int price = ((Number) product.get("price")).intValue();

                total += price * item.getAmount();
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("total_price", String.valueOf(total));

        return ResponseEntity.ok(response);
    }

    // ✅ i18n: sepeti localize ederek dön
    public ResponseEntity<ShoppingCart> getCartById(Long shoppingCartId, String acceptLanguage) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        //localizeCart(shoppingCart, acceptLanguage);
        return ResponseEntity.ok(shoppingCart);
    }

    public ResponseEntity<ShoppingCart> getCartByShoppingCartName(String shoppingCartName, String acceptLanguage) {
        Optional<ShoppingCart> opt = shoppingCartRepository.findByShoppingCartName(shoppingCartName);

        if (opt.isPresent()) {
            ShoppingCart cart = opt.get();
            return ResponseEntity.ok(cart);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<List<ShoppingCart>> getAllCarts(String acceptLanguage) {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        //shoppingCarts.forEach(c -> localizeCart(c, acceptLanguage));
        return ResponseEntity.ok(shoppingCarts);
    }

    public ResponseEntity<String> deleteCartById(Long shoppingCartId) {
        if (shoppingCartRepository.existsById(shoppingCartId)) {
            shoppingCartRepository.deleteById(shoppingCartId);
            return ResponseEntity.ok("Shopping Cart deleted successfully");
        } else {
            throw new RuntimeException("Shopping Cart not found in DB");
        }
    }

    public ResponseEntity<String> deleteAllCarts() {
        shoppingCartRepository.deleteAll();
        return ResponseEntity.ok("All Shopping Carts deleted successfully");
    }

}
