package com.example.movies_recommendation_API.movie_favorite_list;

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
public class MovieFavoriteListService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieFavoriteListRepository movieFavoriteListRepository;
    

    public ResponseEntity<?> getMoviesByUserId(
            Integer pageNumber, Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Lấy MovieFavoriteList của người dùng
        MovieFavoriteList watchList = movieFavoriteListRepository.findByUserId(user.get_id());

        // Gọi repository để lấy Movies với phân trang
        return ResponseEntity.ok().body(
                movieRepository.findByCustomIdIn(watchList.getMovieIds(), pageable)
        );
    }

    public ResponseEntity<?> addMovieToFavoriteList(Integer movieId) {
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieFavoriteList movieFavoriteList = movieFavoriteListRepository.findByUserId(user.get_id());
        if (movieFavoriteList == null) {
            movieFavoriteList = MovieFavoriteList.builder()
                    .userId(user.get_id())
                    .movieIds(List.of(movie.getId()))
                    .build();
        } else {

            if (! movieFavoriteList.getMovieIds().contains(movie.getId())) {
                movieFavoriteList.getMovieIds().add(movie.getId());
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseError("Movie đã tồn tại trong danh sách yêu thích.")
                );
            }
        }
        movieFavoriteListRepository.save(movieFavoriteList);
        return ResponseEntity.ok().body(
                new ResponseSuccess()
        );
    }

    public ResponseEntity<?> deleteMovieToFavoriteList(Integer movieId) {
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseError("Movie không tồn tại.")
            );
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieFavoriteList movieFavoriteList = movieFavoriteListRepository.findByUserId(user.get_id());
        if (movieFavoriteList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách yêu thích của người dùng trống.")
            );
        } else {
            if (movieFavoriteList.getMovieIds().contains(movie.getId())) {
                movieFavoriteList.getMovieIds().remove(movie.getId());
                if (movieFavoriteList.getMovieIds().isEmpty()) {
                    movieFavoriteListRepository.delete(movieFavoriteList);
                } else {
                    movieFavoriteListRepository.save(movieFavoriteList);
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
        MovieFavoriteList movieFavoriteList = movieFavoriteListRepository.findByUserId(user.get_id());
        if (movieFavoriteList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách xem trống.")
            );
        } else {
            movieFavoriteListRepository.deleteAll();
            return ResponseEntity.ok().body(
                    new ResponseSuccess()
            );
        }
    }
}
