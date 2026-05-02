package com.n11bootcamp.favorite_list_service.service;

import com.n11bootcamp.favorite_list_service.client.ProductClient;
import com.n11bootcamp.favorite_list_service.dto.ProductDTO;
import com.n11bootcamp.favorite_list_service.entity.FavoriteList;
import com.n11bootcamp.favorite_list_service.entity.FavoriteListItem;
import com.n11bootcamp.favorite_list_service.repository.FavoriteListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class FavoriteListService {

    private final FavoriteListRepository favoriteListRepository;
    private final ProductClient productClient;

    public FavoriteListService(FavoriteListRepository favoriteListRepository, ProductClient productClient) {
        this.favoriteListRepository = favoriteListRepository;
        this.productClient = productClient;
    }

    // 🔍 GET OR CREATE: Kullanıcıya ait belirli isimdeki listeyi bulur, yoksa oluşturur.
    public FavoriteList getOrCreateList(String username, String listName) {
        // Repository'de findByUsernameAndFavoriteListName metodu olmalı
        return favoriteListRepository.findByUsernameAndFavoriteListName(username, listName)
                .orElseGet(() -> {
                    FavoriteList newList = new FavoriteList();
                    newList.setFavoriteListName(listName);
                    newList.setUsername(username); // Entity'ye username alanı eklenmeli
                    newList.setProducts(new HashSet<>());
                    return favoriteListRepository.save(newList);
                });
    }

    // 🔵 Ürün Ekleme (Username ve ListName ile)
    public FavoriteList addItem(String username, String listName, Long productId) {
        // Önce kullanıcının ilgili listesini getir veya oluştur
        FavoriteList favoriteList = getOrCreateList(username, listName);

        // 1. Ürün listede zaten var mı?
        boolean exists = favoriteList.getProducts().stream()
                .anyMatch(p -> p.getProductId().equals(productId));

        if (exists) {
            return favoriteList;
        }

        // 2. Product Service'den güncel bilgileri çek
        ProductDTO productDTO = productClient.getProductById(productId);
        if (productDTO == null) {
            throw new RuntimeException("Ürün bulunamadı: " + productId);
        }

        // 3. Yeni bir FavoriteListItem oluştur ve zenginleştir
        FavoriteListItem newItem = new FavoriteListItem();
        newItem.setProductId(productId);
        newItem.setName(productDTO.title());
        newItem.setPrice(productDTO.price());
        newItem.setImage(productDTO.img());
        newItem.setFavoriteList(favoriteList);

        favoriteList.getProducts().add(newItem);
        return favoriteListRepository.save(favoriteList);
    }

    // 🔴 Ürünü favorilerden silme
    public FavoriteList removeItem(String username, String listName, Long productId) {
        FavoriteList favoriteList = favoriteListRepository.findByUsernameAndFavoriteListName(username, listName)
                .orElseThrow(() -> new RuntimeException("Liste bulunamadı!"));

        favoriteList.getProducts().removeIf(item -> item.getProductId().equals(productId));
        return favoriteListRepository.save(favoriteList);
    }

    // 🟡 Belirli bir kullanıcının tüm listelerini getir
    public List<FavoriteList> getAllByUsername(String username) {
        return favoriteListRepository.findAllByUsername(username);
    }

    public List<FavoriteList> getAll() {
        return favoriteListRepository.findAll();
    }

    // 🟡 ID ile Liste Getir
    public FavoriteList getListById(Long id) {
        return favoriteListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favori listesi bulunamadı! ID: " + id));
    }

}