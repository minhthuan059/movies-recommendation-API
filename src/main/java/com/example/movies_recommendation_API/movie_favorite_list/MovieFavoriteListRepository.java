package com.example.movies_recommendation_API.movie_favorite_list;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieFavoriteListRepository extends MongoRepository<MovieFavoriteList, String> {
    Page<MovieFavoriteList> findAll(Pageable pageable);

    MovieFavoriteList findByUserId(String userId);
}
