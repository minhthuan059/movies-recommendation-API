package com.example.movies_recommendation_API.reviews;

import com.example.movies_recommendation_API.movie_rating_list.MovieRatingListService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRatingListService movieRatingListService;

    public ResponseEntity<?> addReviewToMovie (
            Integer movieId,
            String content,
            Double rating
    ) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        List<Review> reviews = movie.getReviews();

        boolean userHasReviewed = reviews.stream()
                .anyMatch(review -> user.get_id().equals(review.getId()));

        if (userHasReviewed) {
            return  ResponseEntity.ok().body(
                    new ResponseError("Người xem đã review phim.")
            );
        }

        if (rating < 0 || rating > 10) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Điểm đánh giá phải trong khoảng 0 đến 10.")
            );
        }

        AuthorDetails details = new AuthorDetails();
        details.setName(user.getUsername());
        details.setUsername(user.getUsername());
        details.setAvatar_path(user.getPicture() != null ? user.getPicture() : "");
        details.setRating(rating);

        Review review = new Review();
        review.setAuthor(user.getUsername());
        review.setAuthor_details(details);
        review.setContent(content);
        review.setId(user.get_id());
        review.setCreated_at(LocalDateTime.now());
        review.setUpdated_at(LocalDateTime.now());
        review.setUrl("");


        Double voteAverage = movie.getVoteAverage();
        Integer voteCount = movie.getVoteCount();

        voteAverage = (voteAverage * voteCount + rating) / (voteCount + 1);
        voteCount += 1;
        movie.setVoteAverage(voteAverage);
        movie.setVoteCount(voteCount);
        movie.getReviews().add(review);

        movieRepository.save(movie);

        movieRatingListService.addRatingToMovieRatingList(
                movieId, rating
        );
        return ResponseEntity.ok().body(
                new ResponseSuccess()
        );
    }


    public ResponseEntity<?> deleteReviewInMovie(Integer movieId) {
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Review> reviewOptional = movie.getReviews().stream()
                .filter(review -> review.getId().equals(user.get_id()))  // So sánh id của review với id đã cho
                .findFirst();

        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Người dùng chưa đánh giá movie.")
            );
        }
        Review review = reviewOptional.get();
        Integer voteCount = movie.getVoteCount();
        Double voteAverage = movie.getVoteAverage();

        voteAverage = (voteAverage * voteCount - review.getAuthor_details().getRating()) / (voteCount - 1);
        voteCount -= 1;

        movie.setVoteAverage(voteAverage);
        movie.setVoteCount(voteCount);

        movie.getReviews().remove(review);

        movieRepository.save(movie);
        movieRatingListService.deleteMovieInRatingList(movieId);
        return ResponseEntity.ok().body(
                new ResponseSuccess()
        );
    }

    public void deleteReviewInMovieWithOutRattingList(Integer movieId) {
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ;
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Review> reviewOptional = movie.getReviews().stream()
                .filter(review -> review.getId().equals(user.get_id()))  // So sánh id của review với id đã cho
                .findFirst();

        if (reviewOptional.isEmpty()) {
            return;
        }
        Review review = reviewOptional.get();
        Integer voteCount = movie.getVoteCount();
        Double voteAverage = movie.getVoteAverage();

        voteAverage = (voteAverage * voteCount - review.getAuthor_details().getRating()) / (voteCount - 1);
        voteCount -= 1;
        movie.setVoteAverage(voteAverage);
        movie.setVoteCount(voteCount);
        movie.getReviews().remove(review);
        movieRepository.save(movie);
    }

}
