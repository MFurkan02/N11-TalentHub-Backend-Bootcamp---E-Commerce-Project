package com.n11bootcamp.favorite_list_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Set;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "favorite_list_name"})
})
public class FavoriteList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "favorite_list_name")
    private String favoriteListName;

    @OneToMany(mappedBy = "favoriteList", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<FavoriteListItem> products;


    // Constructors

    public FavoriteList(){

    }

    public FavoriteList(Long id,String name,Set<FavoriteListItem> products){
        this.id = id;
        this.favoriteListName = name;
        this.products = products;
    }


    // Getters and Setters

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username ){
        this.username = username;
    }


    public String getFavoriteListName(){
        return favoriteListName;
    }

    public void setFavoriteListName(String name){
        this.favoriteListName = name;
    }

    public Set<FavoriteListItem> getProducts(){
        return products;
    }

    public void setProducts(Set<FavoriteListItem> products){
        this.products = products;
    }

}
