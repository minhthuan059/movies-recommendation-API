package com.example.movies_recommendation_API.movies_watch_list;

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
public class MoviesWatchListService {
    @Autowired
    private MovieRepository moviesRepository;

    @Autowired
    private MoviesWatchListRepository moviesWatchListRepository;

    @Autowired
    private  MovieService movieService;

    public ResponseEntity<?> getMoviesByUserId(
            Integer pageNumber, Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Lấy MoviesWatchList của người dùng
        MoviesWatchList watchList = moviesWatchListRepository.findByUserId(user.get_id());

        // Gọi repository để lấy Movies với phân trang
        return ResponseEntity.ok().body(
                moviesRepository.findByCustomIdIn(watchList.getMovieIds(), pageable)
        );
    }

    public ResponseEntity<?> addMovieToWatchList(Integer movieId) {
        Movie movie = moviesRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MoviesWatchList moviesWatchList = moviesWatchListRepository.findByUserId(user.get_id());
        if (moviesWatchList == null) {
            moviesWatchList = MoviesWatchList.builder()
                    .userId(user.get_id())
                    .movieIds(List.of(movie.getId()))
                    .build();
        } else {
            if (!moviesWatchList.getMovieIds().contains(movie.getId())) {
                moviesWatchList.getMovieIds().add(movie.getId());
                moviesWatchListRepository.save(moviesWatchList);
                return ResponseEntity.ok().body(
                        new ResponseSuccess()
                );
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseError("Movie đã tồn tại trong danh sách xem.")
                );
            }
        }
        return null;
    }

    public ResponseEntity<?> deleteMovieToWatchList(Integer movieId) {
        Movie movie = moviesRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MoviesWatchList moviesWatchList = moviesWatchListRepository.findByUserId(user.get_id());
        if (moviesWatchList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách xem trống.")
            );
        } else {
            if (moviesWatchList.getMovieIds().contains(movie.getId())) {
                moviesWatchList.getMovieIds().remove(movie.getId());
                if (moviesWatchList.getMovieIds().isEmpty()) {
                    moviesWatchListRepository.delete(moviesWatchList);
                } else {
                    moviesWatchListRepository.save(moviesWatchList);
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
        MoviesWatchList moviesWatchList = moviesWatchListRepository.findByUserId(user.get_id());
        if (moviesWatchList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách xem trống.")
            );
        } else {
            moviesWatchListRepository.deleteAll();
            return ResponseEntity.ok().body(
                    new ResponseSuccess()
            );
        }
    }
}
