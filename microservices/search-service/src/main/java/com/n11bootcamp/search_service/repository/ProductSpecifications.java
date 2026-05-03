package com.n11bootcamp.search_service.repository;

import com.n11bootcamp.search_service.entity.SearchProduct;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {
    public static Specification<SearchProduct> searchProducts(String query) {
        return (root, criteriaQuery, cb) -> {
            if (query == null || query.isEmpty()) return cb.conjunction();

            String pattern = "%" + query.toLowerCase() + "%";

            // Başlık, Marka veya Kategori içinde arama yap (OR mantığı)
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("brand")), pattern),
                    cb.like(cb.lower(root.get("category")), pattern),
                    cb.like(cb.lower(root.get("categoryKey")), pattern)
            );
        };
    }
}