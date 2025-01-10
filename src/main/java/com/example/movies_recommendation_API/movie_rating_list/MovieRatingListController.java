package com.example.movies_recommendation_API.movie_rating_list;

import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movie-rating-list")
public class MovieRatingListController {

    @Autowired
    private MovieRatingListService movieRatingListService;


//    @PostMapping("")
//    public ResponseEntity<?> postAddToMoviesRatingList(@RequestBody Map<String, Object> body) {
//        if (body.get("movieId") == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new ResponseError("Không nhận được id phim.")
//            );
//        } else if (body.get("rating") == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new ResponseError("Không nhận được điểm đáng giá.")
//            );
//        } else {
//            return movieRatingListService.addRatingToMovieRatingList(
//                    Integer.parseInt(body.get("movieId").toString()),
//                    Double.parseDouble(body.get("rating").toString())
//            );
//        }
//    }

    @GetMapping("")
    public ResponseEntity<?> getAllMovieInRatingList(@RequestParam Map<String, String> param) {
        return movieRatingListService.getMoviesByUserId(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }


//    @DeleteMapping("/{movieId}")
//    public ResponseEntity<?> deleteMovieInRatingList(@PathVariable String movieId){
//        if (movieId == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new ResponseError("Không nhận được id phim.")
//            );
//        } else {
//            return movieRatingListService.deleteMovieInRatingList(
//                    Integer.parseInt(movieId)
//            );
//        }
//    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllMovieInMoviesWatchList(){
        return movieRatingListService.deleteAllMovieToRatingList();
    }

}
