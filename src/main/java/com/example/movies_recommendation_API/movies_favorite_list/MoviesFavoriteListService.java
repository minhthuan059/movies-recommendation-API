package com.example.movies_recommendation_API.movies_favorite_list;

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
public class MoviesFavoriteListService {
    @Autowired
    private MovieRepository moviesRepository;

    @Autowired
    private MoviesFavoriteListRepository moviesFavoriteListRepository;

    @Autowired
    private  MovieService movieService;

    public ResponseEntity<?> getMoviesByUserId(
            Integer pageNumber, Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Lấy MoviesFavoriteList của người dùng
        MoviesFavoriteList watchList = moviesFavoriteListRepository.findByUserId(user.get_id());

        // Gọi repository để lấy Movies với phân trang
        return ResponseEntity.ok().body(
                moviesRepository.findByCustomIdIn(watchList.getMovieIds(), pageable)
        );
    }

    public ResponseEntity<?> addMovieToFavoriteList(Integer movieId) {
        Movie movie = moviesRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MoviesFavoriteList moviesFavoriteList = moviesFavoriteListRepository.findByUserId(user.get_id());
        if (moviesFavoriteList == null) {
            moviesFavoriteList = MoviesFavoriteList.builder()
                    .userId(user.get_id())
                    .movieIds(List.of(movie.getId()))
                    .build();
        } else {

            if (! moviesFavoriteList.getMovieIds().contains(movie.getId())) {
                moviesFavoriteList.getMovieIds().add(movie.getId());
                moviesFavoriteListRepository.save(moviesFavoriteList);
                return ResponseEntity.ok().body(
                        new ResponseSuccess()
                );
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseError("Movie đã tồn tại trong danh sách yêu thích.")
                );
            }
        }
        return null;
    }

    public ResponseEntity<?> deleteMovieToFavoriteList(Integer movieId) {
        Movie movie = moviesRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MoviesFavoriteList moviesFavoriteList = moviesFavoriteListRepository.findByUserId(user.get_id());
        if (moviesFavoriteList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách yêu thích của người dùng trống.")
            );
        } else {
            if (moviesFavoriteList.getMovieIds().contains(movie.getId())) {
                moviesFavoriteList.getMovieIds().remove(movie.getId());
                if (moviesFavoriteList.getMovieIds().isEmpty()) {
                    moviesFavoriteListRepository.delete(moviesFavoriteList);
                } else {
                    moviesFavoriteListRepository.save(moviesFavoriteList);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseSuccess()
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseError("Movie không tồn tại trong danh sách yêu thích.")
                );
            }
        }
    }

    public ResponseEntity<?> deleteAllMovieToFavoriteList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MoviesFavoriteList moviesFavoriteList = moviesFavoriteListRepository.findByUserId(user.get_id());
        if (moviesFavoriteList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách xem trống.")
            );
        } else {
            moviesFavoriteListRepository.deleteAll();
            return ResponseEntity.ok().body(
                    new ResponseSuccess()
            );
        }
    }
}
