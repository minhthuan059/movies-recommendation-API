package com.example.movies_recommendation_API.movies_favorite_list;

import com.example.movies_recommendation_API.movies.Movie;
import com.example.movies_recommendation_API.movies.MovieRepository;
import com.example.movies_recommendation_API.movies.MovieService;
import com.example.movies_recommendation_API.movies_watch_list.MoviesWatchList;
import com.example.movies_recommendation_API.movies_watch_list.MoviesWatchListRepository;
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

    public ResponseEntity<?> AddMovieToFavoriteList(Integer movieId) {
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
            moviesFavoriteList.getMovieIds().add(movie.getId());
        }
        moviesFavoriteListRepository.save(moviesFavoriteList);
        return ResponseEntity.ok().body(
                new ResponseSuccess()
        );

    }
}
