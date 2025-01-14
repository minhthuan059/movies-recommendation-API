package com.example.movies_recommendation_API.movie_watch_list;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieWatchListRepository extends MongoRepository<MovieWatchList, String> {
    Page<MovieWatchList> findAll(Pageable pageable);
    MovieWatchList findByUserId(String userId);
}
