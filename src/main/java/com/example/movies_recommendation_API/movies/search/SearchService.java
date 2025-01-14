package com.example.movies_recommendation_API.movies.search;

import com.example.movies_recommendation_API.movies.Movie;
import com.example.movies_recommendation_API.movies.MovieRepository;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    @Autowired
    private MovieRepository movieRepository;

    // Tìm kiếm gần đúng theo keyword
    public ResponseEntity<?> searchByTitle(String keyword) {
        return ResponseEntity.ok().body(
                new ResponseSuccess(
                movieRepository.findListByTitleContainingIgnoreCase(keyword))
        );
    }

    public ResponseEntity<?> searchByCastName(String keyword) {
        return ResponseEntity.ok().body(
                new ResponseSuccess(
                        movieRepository.findListByCastName(keyword))
        );
    }

    public ResponseEntity<?> searchByTitleAndCastName(String title, String cast, Integer pageNumber, Integer pageSize) {
        // Tạo Pageable từ số trang và số phần tử mỗi trang
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Tìm kiếm phim với các điều kiện
        Page<Movie> result = movieRepository.findListByTitleAndCastName(title, cast, pageable);
        return ResponseEntity.ok().body(
                new ResponseSuccess(
                    result
                )
        );
    }
}
