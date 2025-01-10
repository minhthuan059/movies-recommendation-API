package com.example.movies_recommendation_API.movie_favorite_list;


import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("movie-favorite-list")
public class MovieFavoriteListController {

    @Autowired
    private MovieFavoriteListService movieFavoriteListService;

    @GetMapping("")
    public ResponseEntity<?> getMovieFavoriteList(@RequestParam Map<String, String> param){
        return movieFavoriteListService.getMoviesByUserId(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @PostMapping("")
    public ResponseEntity<?> postAddToMovieFavoriteList(@RequestBody Map<String, Object> body){
        if (body.get("movieId") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được id phim.")
            );
        } else {
            return movieFavoriteListService.addMovieToFavoriteList(
                    Integer.parseInt(body.get("movieId").toString())
            );
        }
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<?> deleteMovieInMoviesWatchList(@PathVariable String movieId){
        if (movieId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được id phim.")
            );
        } else {
            return movieFavoriteListService.deleteMovieToFavoriteList(
                    Integer.parseInt(movieId)
            );
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllMovieInMoviesWatchList(){
        return movieFavoriteListService.deleteAllMovieToFavoriteList();
    }

}
