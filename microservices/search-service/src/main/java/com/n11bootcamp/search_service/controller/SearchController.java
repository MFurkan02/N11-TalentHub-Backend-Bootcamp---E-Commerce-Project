package com.n11bootcamp.search_service.controller;

import com.n11bootcamp.search_service.entity.SearchProduct;
import com.n11bootcamp.search_service.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search Service", description = "Elasticsearch tabanlı global ürün arama işlemlerini yönetir.")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(
            summary = "Global Ürün Araması",
            description = "Ürün adı, marka veya kategoriye göre Elasticsearch üzerinde hızlı arama yapar ve sayfalı sonuç döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arama sonuçları başarıyla getirildi"),
            @ApiResponse(responseCode = "500", description = "Elasticsearch servisine ulaşılamadı")
    })
    @GetMapping
    public ResponseEntity<Page<SearchProduct>> globalSearch(
            @Parameter(description = "Arama kelimesi (Ürün adı, marka vb.)", example = "iPhone 15")
            @RequestParam(value = "q", required = false) String query,

            @Parameter(description = "Sayfa numarası (0'dan başlar)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Sayfa başına ürün sayısı", example = "12")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(searchService.search(query, pageable));
    }
}