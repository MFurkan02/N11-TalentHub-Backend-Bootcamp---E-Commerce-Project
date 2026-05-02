package com.n11bootcamp.favorite_list_service.controller;

import com.n11bootcamp.favorite_list_service.entity.FavoriteList;
import com.n11bootcamp.favorite_list_service.service.FavoriteListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/favorite-list")
public class FavoriteListController {

    private final FavoriteListService favoriteListService;

    public FavoriteListController(FavoriteListService favoriteListService) {
        this.favoriteListService = favoriteListService;
    }

    /**
     * 🔵 Ürün Ekle (Get or Create mantığıyla)
     * Eğer belirtilen listName o kullanıcı için yoksa otomatik oluşturur ve ürünü ekler.
     * POST: api/favorite-list/add?username=ahmet&listName=Favorilerim&productId=101
     */
    @PostMapping("/add")
    public ResponseEntity<FavoriteList> addItem(
            @RequestParam("username") String username,
            @RequestParam("listName") String listName,
            @RequestParam("productId") Long productId
    ) {
        return ResponseEntity.ok(
                favoriteListService.addItem(username, listName, productId)
        );
    }

    /**
     * 🔴 Ürün Çıkar
     * DELETE: api/favorite-list/remove?username=ahmet&listName=Favorilerim&productId=101
     */
    @DeleteMapping("/remove")
    public ResponseEntity<FavoriteList> removeItem(
            @RequestParam("username") String username,
            @RequestParam("listName") String listName,
            @RequestParam("productId") Long productId
    ) {
        return ResponseEntity.ok(
                favoriteListService.removeItem(username, listName, productId)
        );
    }

    /**
     * 🟡 Kullanıcının Tüm Listelerini Getir
     * GET: api/favorite-list/user/ahmet
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<FavoriteList>> getAllByUsername(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(favoriteListService.getAllByUsername(username));
    }

    /**
     * 🟡 Spesifik Bir Listeyi Getir
     * GET: api/favorite-list/find?username=ahmet&listName=Yazlık
     */
    @GetMapping("/find")
    public ResponseEntity<FavoriteList> getOrCreate(
            @RequestParam("username") String username,
            @RequestParam("listName") String listName
    ) {
        return ResponseEntity.ok(favoriteListService.getOrCreateList(username, listName));
    }

    /**
     * 🟡 Sistemdeki Tüm Listeleri Getir (Admin fonksiyonu gibi)
     */
    @GetMapping
    public ResponseEntity<List<FavoriteList>> getAll() {
        return ResponseEntity.ok(favoriteListService.getAll());
    }

    /**
     * 🟡 ID ile Liste Getir (Hala ihtiyaç duyulursa)
     */
    @GetMapping("/{id}")
    public ResponseEntity<FavoriteList> getListById(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteListService.getListById(id));
    }
}