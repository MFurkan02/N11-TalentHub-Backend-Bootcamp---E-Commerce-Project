package com.n11bootcamp.favorite_list_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class FavoriteListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String name;

    private double price;

    private String image;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_list_id")
    @JsonBackReference
    private FavoriteList favoriteList;


    public FavoriteListItem(){

    }

    public FavoriteListItem(Long id,Long productId,FavoriteList list,String name,String image,double price){
        this.id = id;
        this.productId = productId;
        this.favoriteList = list;
        this.image = image;
        this.price = price;
        this.name = name;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getProductId(){
        return productId;
    }

    public void setProductId(Long id){
        this.productId = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getImage(){return image;}

    public void setImage(String image) { this.image = image;}

    public  double getPrice(){return price;}

    public void setPrice(double price){this.price = price;}

    public FavoriteList getFavoriteList(){
        return favoriteList;
    }

    public void setFavoriteList(FavoriteList favoriteList){
        this.favoriteList = favoriteList;
    }
}
