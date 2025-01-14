package com.example.movies_recommendation_API.people;


import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PeopleService {

    @Autowired
    private PeopleRepository peopleRepository;

    public ResponseEntity<?> getById(Integer id) {
        People people = peopleRepository.findOneById(id);
        if (people == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không tìm thấy người có id yêu cầu")
            );
        }
        return ResponseEntity.ok().body(
                new ResponseSuccess(people)
        );
    }

    public ResponseEntity<?> getAllPeople(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<People> people = peopleRepository.findAll(pageable);
        if (people == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không tìm thấy người có id yêu cầu")
            );
        }
        return ResponseEntity.ok().body(
                new ResponseSuccess(people)
        );
    }
}
