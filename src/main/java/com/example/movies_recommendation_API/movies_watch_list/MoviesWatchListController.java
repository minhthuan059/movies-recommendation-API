package com.example.movies_recommendation_API.movies_watch_list;


import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("movie-watch-list")
public class MoviesWatchListController {

    @Autowired
    private MoviesWatchListService moviesWatchListService;

    @GetMapping("")
    public ResponseEntity<?> getMoviesWatchList(@RequestParam Map<String, Object> param){
        return moviesWatchListService.getMoviesByUserId(
                param.get("page") != null ? Integer.parseInt(param.get("page").toString()) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size").toString()) : 10
        );
    }

    @PostMapping("")
    public ResponseEntity<?> postAddToMoviesWatchList(@RequestBody Map<String, Object> body){
        if (body.get("movieId") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được id phim.")
            );
        } else {
            return moviesWatchListService.AddMovieToWatchList(
                    Integer.parseInt(body.get("movieId").toString())
            );
        }
    }

}
