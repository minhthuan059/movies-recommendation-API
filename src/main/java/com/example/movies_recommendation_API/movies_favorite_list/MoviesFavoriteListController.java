package com.example.movies_recommendation_API.movies_favorite_list;


import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("movie-favorite-list")
public class MoviesFavoriteListController {

    @Autowired
    private MoviesFavoriteListService moviesFavoriteListService;

    @GetMapping("")
    public ResponseEntity<?> getMoviesFavoriteList(@RequestParam Map<String, Object> param){
        return moviesFavoriteListService.getMoviesByUserId(
                param.get("page") != null ? Integer.parseInt(param.get("page").toString()) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size").toString()) : 10
        );
    }

    @PostMapping("")
    public ResponseEntity<?> postAddToMoviesFavoriteList(@RequestBody Map<String, Object> body){
        if (body.get("movieId") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được id phim.")
            );
        } else {
            return moviesFavoriteListService.addMovieToFavoriteList(
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
            return moviesFavoriteListService.deleteMovieToFavoriteList(
                    Integer.parseInt(movieId)
            );
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllMovieInMoviesWatchList(){
        return moviesFavoriteListService.deleteAllMovieToFavoriteList();
    }

}
