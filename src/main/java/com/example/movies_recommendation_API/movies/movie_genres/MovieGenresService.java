package com.example.movies_recommendation_API.movies.movie_genres;

import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MovieGenresService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseEntity<?> getAllMovieGenres() {
        Query query = new Query();
        List<Map> genres = mongoTemplate.find(query, Map.class, "movie_genres");
        return ResponseEntity.ok().body(
                new ResponseSuccess(
                        genres
                )
        );
    }
}
