package com.example.movies_recommendation_API.movie_rating_list;

import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movie-rating")
public class MovieRatingListController {

    @Autowired
    private MovieRatingListService movieRatingListService;


    @PostMapping("")
    public ResponseEntity<?> postAddToMoviesWatchList(@RequestBody Map<String, Object> body) {
        if (body.get("movieId") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được id phim.")
            );
        } else if (body.get("rating") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được điểm đáng giá.")
            );
        } else {
            return movieRatingListService.addRatingToMovieRatingList(
                    Integer.parseInt(body.get("movieId").toString()),
                    Double.parseDouble(body.get("rating").toString())
            );
        }
    }

}
