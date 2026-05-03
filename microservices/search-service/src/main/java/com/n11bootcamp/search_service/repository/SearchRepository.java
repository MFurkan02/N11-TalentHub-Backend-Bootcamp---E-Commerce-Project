package com.n11bootcamp.search_service.repository;

import com.n11bootcamp.search_service.entity.SearchProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<SearchProduct, Long>, JpaSpecificationExecutor<SearchProduct> {

}