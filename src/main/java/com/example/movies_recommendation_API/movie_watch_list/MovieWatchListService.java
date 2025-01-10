package com.example.movies_recommendation_API.movie_watch_list;

import com.example.movies_recommendation_API.movies.Movie;
import com.example.movies_recommendation_API.movies.MovieRepository;
import com.example.movies_recommendation_API.movies.MovieService;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import com.example.movies_recommendation_API.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieWatchListService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieWatchListRepository movieWatchListRepository;

    @Autowired
    private  MovieService movieService;

    public ResponseEntity<?> getMoviesByUserId(
            Integer pageNumber, Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Lấy MovieWatchList của người dùng
        MovieWatchList watchList = movieWatchListRepository.findByUserId(user.get_id());

        // Gọi repository để lấy Movies với phân trang
        return ResponseEntity.ok().body(
                movieRepository.findByCustomIdIn(watchList.getMovieIds(), pageable)
        );
    }

    public ResponseEntity<?> addMovieToWatchList(Integer movieId) {
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieWatchList movieWatchList = movieWatchListRepository.findByUserId(user.get_id());
        if (movieWatchList == null) {
            movieWatchList = MovieWatchList.builder()
                    .userId(user.get_id())
                    .movieIds(List.of(movie.getId()))
                    .build();
        } else {
            if (!movieWatchList.getMovieIds().contains(movie.getId())) {
                movieWatchList.getMovieIds().add(movie.getId());

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseError("Movie đã tồn tại trong danh sách xem.")
                );
            }
        }
        movieWatchListRepository.save(movieWatchList);
        return ResponseEntity.ok().body(
                new ResponseSuccess()
        );

    }

    public ResponseEntity<?> deleteMovieToWatchList(Integer movieId) {
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieWatchList movieWatchList = movieWatchListRepository.findByUserId(user.get_id());
        if (movieWatchList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách xem trống.")
            );
        } else {
            if (movieWatchList.getMovieIds().contains(movieId)) {
                movieWatchList.getMovieIds().remove(movieId);
                if (movieWatchList.getMovieIds().isEmpty()) {
                    movieWatchListRepository.delete(movieWatchList);
                } else {
                    movieWatchListRepository.save(movieWatchList);
                }
                return ResponseEntity.ok().body(
                        new ResponseSuccess()
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseError("Movie không tồn tại trong danh sách xem.")
                );
            }

        }
    }

    public ResponseEntity<?> deleteAllMovieToWatchList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieWatchList movieWatchList = movieWatchListRepository.findByUserId(user.get_id());
        if (movieWatchList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách xem trống.")
            );
        } else {
            movieWatchListRepository.deleteAll();
            return ResponseEntity.ok().body(
                    new ResponseSuccess()
            );
        }
    }
}
