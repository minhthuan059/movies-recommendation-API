package com.example.movies_recommendation_API.people;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/people")
public class PeopleController {

    @Autowired
    private PeopleService peopleService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return peopleService.getById(Integer.parseInt(id));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPeople(@RequestParam Map<String, String> param) {
        return peopleService.getAllPeople(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }
}
