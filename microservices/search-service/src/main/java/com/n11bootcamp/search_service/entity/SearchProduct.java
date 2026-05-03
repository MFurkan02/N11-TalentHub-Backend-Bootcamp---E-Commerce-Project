package com.n11bootcamp.search_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class SearchProduct {

    public SearchProduct() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "img", length = 512)
    private String img;

    @Column(name = "labels", length = 255)
    private String labels;

    @Column(name = "brand", length = 255)
    private String brand;

    @Column(name = "color", length = 100)
    private String color;

    @Column(name = "title", nullable = false, length = 255)
    private String title = "-";

    @Column(name = "category", nullable = false, length = 255)
    private String category = "giysi";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_key", length = 100)
    private String categoryKey;

    // getters and setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getLabels() { return labels; }
    public void setLabels(String labels) { this.labels = labels; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

}
