package com.example.movies_recommendation_API.movie_rating_list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movie-rating-list")
public class MovieRatingListController {

    @Autowired
    private MovieRatingListService movieRatingListService;

    @GetMapping("")
    public ResponseEntity<?> getAllMovieInRatingList(@RequestParam Map<String, String> param) {
        return movieRatingListService.getMoviesByUserId(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllMovieInMoviesWatchList(){
        return movieRatingListService.deleteAllMovieToRatingList();
    }

}
