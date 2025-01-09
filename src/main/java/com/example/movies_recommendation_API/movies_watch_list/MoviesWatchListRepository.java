package com.example.movies_recommendation_API.movies_watch_list;

import com.example.movies_recommendation_API.movies.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesWatchListRepository extends MongoRepository<MoviesWatchList, String> {
    Page<MoviesWatchList> findAll(Pageable pageable);

    MoviesWatchList findByUserId(String userId);
}
