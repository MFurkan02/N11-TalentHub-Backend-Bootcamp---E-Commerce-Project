package com.n11bootcamp.search_service.service;

import com.n11bootcamp.search_service.entity.SearchProduct;
import com.n11bootcamp.search_service.repository.ProductSpecifications;
import com.n11bootcamp.search_service.repository.SearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SearchService {
    private final SearchRepository searchRepository;

    public SearchService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public Page<SearchProduct> search(String query, Pageable pageable) {
        return searchRepository.findAll(ProductSpecifications.searchProducts(query), pageable);
    }
}