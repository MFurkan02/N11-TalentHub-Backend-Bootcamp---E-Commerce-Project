package com.n11bootcamp.favorite_list_service.controller;

import com.n11bootcamp.favorite_list_service.entity.FavoriteList;
import com.n11bootcamp.favorite_list_service.service.FavoriteListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/favorite-list")
@Tag(name = "Favorite List Service", description = "Kullanıcıların özel favori listelerini oluşturmasını ve ürün yönetmesini sağlar.")
public class FavoriteListController {

    private final FavoriteListService favoriteListService;

    public FavoriteListController(FavoriteListService favoriteListService) {
        this.favoriteListService = favoriteListService;
    }

    @Operation(
            summary = "Favori Listesine Ürün Ekle",
            description = "Belirtilen liste adına ürünü ekler. Eğer liste mevcut değilse 'Get or Create' mantığıyla otomatik oluşturur."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürün başarıyla listeye eklendi"),
            @ApiResponse(responseCode = "404", description = "Ürün veya kullanıcı bulunamadı")
    })
    @PostMapping("/add")
    public ResponseEntity<FavoriteList> addItem(
            @Parameter(description = "İşlemi yapan kullanıcı adı", example = "furkan123") @RequestParam("username") String username,
            @Parameter(description = "Liste adı", example = "Teknoloji Ürünlerim") @RequestParam("listName") String listName,
            @Parameter(description = "Eklenecek ürün ID'si", example = "101") @RequestParam("productId") Long productId
    ) {
        return ResponseEntity.ok(favoriteListService.addItem(username, listName, productId));
    }

    @Operation(summary = "Favori Listesinden Ürün Çıkar", description = "Ürünü belirtilen kullanıcı listesinden siler.")
    @DeleteMapping("/remove")
    public ResponseEntity<FavoriteList> removeItem(
            @Parameter(description = "Kullanıcı adı") @RequestParam("username") String username,
            @Parameter(description = "Liste adı") @RequestParam("listName") String listName,
            @Parameter(description = "Çıkarılacak ürün ID'si") @RequestParam("productId") Long productId
    ) {
        return ResponseEntity.ok(favoriteListService.removeItem(username, listName, productId));
    }

    @Operation(summary = "Kullanıcının Tüm Listelerini Getir", description = "Kullanıcıya ait tüm favori listelerini ve içindeki ürünleri döner.")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<FavoriteList>> getAllByUsername(
            @Parameter(description = "Sorgulanacak kullanıcı adı") @PathVariable String username
    ) {
        return ResponseEntity.ok(favoriteListService.getAllByUsername(username));
    }

    @Operation(summary = "Spesifik Bir Listeyi Getir", description = "Belirli bir kullanıcıya ait tek bir favori listesinin detaylarını çeker.")
    @GetMapping("/find")
    public ResponseEntity<FavoriteList> getOrCreate(
            @RequestParam("username") String username,
            @RequestParam("listName") String listName
    ) {
        return ResponseEntity.ok(favoriteListService.getOrCreateList(username, listName));
    }

    @Operation(summary = "Tüm Listeleri Listele", description = "Sistemdeki tüm kullanıcıların tüm favori listelerini getirir (Admin).")
    @GetMapping
    public ResponseEntity<List<FavoriteList>> getAll() {
        return ResponseEntity.ok(favoriteListService.getAll());
    }

    @Operation(summary = "ID ile Liste Sorgula", description = "Benzersiz liste ID'si üzerinden detayları getirir.")
    @GetMapping("/{id}")
    public ResponseEntity<FavoriteList> getListById(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteListService.getListById(id));
    }
}