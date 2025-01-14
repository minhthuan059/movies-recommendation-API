package com.example.movies_recommendation_API.movie_watch_list;


import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("movie-watch-list")
public class MovieWatchListController {

    @Autowired
    private MovieWatchListService movieWatchListService;

    @GetMapping("")
    public ResponseEntity<?> getMoviesWatchList(@RequestParam Map<String, String> param){
        return movieWatchListService.getMoviesByUserId(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<?> getCheckInFavoriteList(@PathVariable String movieId){
        return movieWatchListService.getCheckMoviesByInList(Integer.parseInt(movieId));
    }

    @PostMapping("")
    public ResponseEntity<?> postAddToMoviesWatchList(@RequestBody Map<String, Object> body){
        if (body.get("movieId") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được id phim.")
            );
        } else {
            return movieWatchListService.addMovieToWatchList(
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
            return movieWatchListService.deleteMovieToWatchList(
                    Integer.parseInt(movieId)
            );
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllMovieInMoviesWatchList(){
        return movieWatchListService.deleteAllMovieToWatchList();
    }
}
