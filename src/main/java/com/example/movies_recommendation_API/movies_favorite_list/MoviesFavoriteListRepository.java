package com.example.movies_recommendation_API.movies_favorite_list;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesFavoriteListRepository extends MongoRepository<MoviesFavoriteList, String> {
    Page<MoviesFavoriteList> findAll(Pageable pageable);

    MoviesFavoriteList findByUserId(String userId);
}
