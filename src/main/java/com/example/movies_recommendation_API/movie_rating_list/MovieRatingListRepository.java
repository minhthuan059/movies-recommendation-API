package com.example.movies_recommendation_API.movie_rating_list;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRatingListRepository extends MongoRepository<MovieRatingList,String> {
    MovieRatingList findByUserId(String userId);
}
