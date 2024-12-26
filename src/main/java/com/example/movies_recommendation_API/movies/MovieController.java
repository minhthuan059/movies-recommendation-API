package com.example.movies_recommendation_API.movies;
import com.example.movies_recommendation_API.Jwt.JwtService;
import com.example.movies_recommendation_API.movies.filter.FilterService;
import com.example.movies_recommendation_API.movies.search.SearchService;
import com.example.movies_recommendation_API.oauth2.GoogleAuthService;
import com.example.movies_recommendation_API.response.LoginResponseSuccess;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private FilterService filterService;

    @GetMapping("/tmdb_id")
    public ResponseEntity<?> getByTmdbId(@RequestParam Map<String, String> param) {
        return movieService.getMovieByTmdbId(param.get("tmdb_id"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> getSearchMovies(@RequestParam Map<String, String> param) {
        if (!param.containsKey("title") && !param.containsKey("cast")) {
            return ResponseEntity.ok().body(
                    new ResponseSuccess(null));
        }
        return searchService.searchByTitleAndCastName(
                param.get("title") != null ? param.get("title") : "",
                param.get("cast")  != null ? param.get("cast") : "",
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @PostMapping("/filter")
    public ResponseEntity<?> getFilterMovies(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("collection") && !body.containsKey("genres")
                && !body.containsKey("minVote") && !body.containsKey("maxVote")
                && !body.containsKey("startDate") && !body.containsKey("endDate")) {
            return ResponseEntity.ok().body(
                    new ResponseSuccess(movieService.getAllMovies()));

        }
        return filterService.filterMovies(
                body.get("collection") != null ? body.get("collection").toString() : "",
                body.get("genres") != null ? (List<String>) body.get("genres") : null,
                body.get("minVote") != null ? Double.parseDouble((String) body.get("minVote")) : 0,
                body.get("maxVote") != null ? Double.parseDouble((String) body.get("maxVote")) : 10,
                body.get("startDate") != null ? (String) body.get("startDate") : "1600-01-01",
                body.get("endDate") != null ? (String) body.get("endDate") : "9999-12-31",
                body.get("page") != null ? Integer.parseInt(body.get("page").toString()) : 0,
                body.get("size") != null ? Integer.parseInt(body.get("size").toString()) : 10
        );
    }
}
