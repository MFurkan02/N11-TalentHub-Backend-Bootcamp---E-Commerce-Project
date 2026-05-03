package com.n11bootcamp.stock_service.dto;

public class ProductDto {
    private Long id;
    private String title;
    private Double price;

    // 1. Boş Constructor (Jackson JSON dönüşümü için şarttır)
    public ProductDto() {
    }

    // 2. Parametreli Constructor
    public ProductDto(Long id, String name, Double price) {
        this.id = id;
        this.title = name;
        this.price = price;
    }

    // 3. Getter ve Setter Metotları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // 4. toString (Loglarda veriyi düzgün görmek için yararlıdır)
    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + title + '\'' +
                ", price=" + price +
                '}';
    }
}