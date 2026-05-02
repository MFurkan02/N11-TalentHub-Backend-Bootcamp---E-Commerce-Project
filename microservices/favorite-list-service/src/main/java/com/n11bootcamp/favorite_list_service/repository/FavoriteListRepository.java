package com.n11bootcamp.favorite_list_service.repository;

import com.n11bootcamp.favorite_list_service.entity.FavoriteList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavoriteListRepository extends JpaRepository<FavoriteList , Long> {

    Page<FavoriteList> findAll(Pageable pageable);

    Optional<FavoriteList> findByFavoriteListName(String name);

    Optional<FavoriteList> findByUsernameAndFavoriteListName(String username,String favoriteListName);

    List<FavoriteList> findAllByUsername(String username);
}
