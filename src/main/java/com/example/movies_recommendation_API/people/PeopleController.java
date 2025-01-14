package com.example.movies_recommendation_API.people;


import com.example.movies_recommendation_API.response.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/list")
    public ResponseEntity<?> getListByIds(@RequestBody Map<String, Object> body) {
        if (body.get("ids") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được danh sách id.")
            );
        }
        return peopleService.getListPeopleByMongoIds(
                (List<String>) body.get("ids"),
                body.get("page") != null ? Integer.parseInt(body.get("page").toString()) : 0,
                body.get("size") != null ? Integer.parseInt(body.get("size").toString()) : 10
        );
    }
}
