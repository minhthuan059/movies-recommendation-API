package com.example.movies_recommendation_API.movie_rating_list;

import com.example.movies_recommendation_API.movies.Movie;
import com.example.movies_recommendation_API.movies.MovieRepository;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import com.example.movies_recommendation_API.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MovieRatingListService {

    @Autowired
    private MovieRatingListRepository movieRatingListRepository;

    @Autowired
    private MovieRepository movieRepository;

    public ResponseEntity<?> addRatingToMovieRatingList(
        Integer movieId, Double rating
    ){
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        if (rating > 10 || rating < 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Điểm đánh giá phải trong khoảng 0 đến 10.")
            );
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieRatingList movieRatingList = movieRatingListRepository.findByUserId(user.get_id());
        if (movieRatingList == null) {
            movieRatingList = MovieRatingList.builder()
                    .userId(user.get_id())
                    .ratingList(List.of(Map.of("movieId", movieId, "rating", rating)))
                    .build();
        } else {
            if (!movieRatingList.getRatingList().stream()
                    .anyMatch(element -> movieId.equals(element.get("movieId")))) {
                movieRatingList.getRatingList().add(Map.of("movieId", movieId, "rating", rating));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseError("Người dùng đã đánh giá phim.")
                );
            }
        }

        Double voteAverage = movie.getVoteAverage();
        Integer voteCount = movie.getVoteCount();

        voteAverage = (voteAverage * voteCount + rating) / (voteCount + 1);
        voteCount += 1;
        movie.setVoteAverage(voteAverage);
        movie.setVoteCount(voteCount);
        movieRepository.save(movie);

        movieRatingListRepository.save(movieRatingList);

        return ResponseEntity.ok().body(
                new ResponseSuccess()
        );
    }

}
