package com.example.movies_recommendation_API.movies;

import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public ResponseEntity<?> getAllMovies(){
        return ResponseEntity.ok().body(
                new ResponseSuccess(movieRepository.findAll())
        );
    }

    public ResponseEntity<?> getMovieById(String id){
        Movie movie = movieRepository.findOneById(Integer.parseInt(id));
        if (movie == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không tìm thấy phim có id yêu cầu")
            );
        }
        return ResponseEntity.ok().body(
                new ResponseSuccess(movie)
        );
    }


}
