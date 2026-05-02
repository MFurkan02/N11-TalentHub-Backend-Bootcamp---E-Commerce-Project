package com.n11bootcamp.payment_service.dto;

public class ProductDTO {

    private Long id;
    private String title;
    private String img;
    private long price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
}