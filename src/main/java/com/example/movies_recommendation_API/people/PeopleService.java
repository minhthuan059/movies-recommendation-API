package com.example.movies_recommendation_API.people;


import com.example.movies_recommendation_API.movies.Movie;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PeopleService {

    @Autowired
    private PeopleRepository peopleRepository;

    public ResponseEntity<?> getById(String id) {
        People people = peopleRepository.findOneById(Integer.parseInt(id));
        if (people == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không tìm thấy phim có id yêu cầu")
            );
        }
        return ResponseEntity.ok().body(
                new ResponseSuccess(people)
        );
    }

}
