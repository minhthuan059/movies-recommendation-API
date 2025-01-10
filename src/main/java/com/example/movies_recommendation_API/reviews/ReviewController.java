package com.example.movies_recommendation_API.reviews;

import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<?> postReviewMovies(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("movieId")) {
            return ResponseEntity.ok().body(
                    new ResponseError("Không nhận được Id phim.")
            );
        }

        if (!body.containsKey("rating") && !body.containsKey("content")) {
            return ResponseEntity.ok().body(
                    new ResponseError("Nội dung rating và content rỗng.")
            );
        }

        return reviewService.addReviewToMovie(
                Integer.parseInt(body.get("movieId").toString()),
                body.get("content") != null ?  body.get("content").toString() : "",
                body.get("rating") != null ?  Double.parseDouble(body.get("rating").toString()) : 0
        );
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<?> deleteReviewMovies(@PathVariable String movieId) {
        if (movieId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được id phim.")
            );
        } else {
            return reviewService.deleteReviewInMovie(
                    Integer.parseInt(movieId)
            );
        }
    }

}
