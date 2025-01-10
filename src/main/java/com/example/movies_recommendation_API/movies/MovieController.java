package com.example.movies_recommendation_API.movies;

import com.example.movies_recommendation_API.movies.filter.FilterService;
import com.example.movies_recommendation_API.movies.movie_genres.MovieGenresService;
import com.example.movies_recommendation_API.movies.movies_popular.MoviesPopularService;
import com.example.movies_recommendation_API.movies.movies_trending_day.MoviesTrendingDayService;
import com.example.movies_recommendation_API.movies.movies_trending_week.MoviesTrendingWeekService;
import com.example.movies_recommendation_API.movies.movies_upcoming.MoviesUpcomingService;
import com.example.movies_recommendation_API.movies.search.SearchService;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieGenresService  movieGenresService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private MoviesTrendingDayService moviesTrendingDayService;

    @Autowired
    private MoviesTrendingWeekService moviesTrendingWeekService;

    @Autowired
    private MoviesPopularService moviesPopularService;

    @Autowired
    private MoviesUpcomingService moviesUpcomingService;


    @GetMapping("/genres")
    public ResponseEntity<?> getAllGenres() {
        return movieGenresService.getAllMovieGenres();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return movieService.getMovieById(id);
    }

    @PostMapping("/list")
    public ResponseEntity<?> getListByIds(@RequestBody Map<String, Object> body) {
        if (body.get("ids") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được danh sách id.")
            );
        }
        return movieService.getListMovieByIds(
                (List<String>) body.get("ids"),
                body.get("page") != null ? Integer.parseInt(body.get("page").toString()) : 0,
                body.get("size") != null ? Integer.parseInt(body.get("size").toString()) : 10
        );
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
    public ResponseEntity<?> postFilterMovies(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("collection") && !body.containsKey("genres")
                && !body.containsKey("keywords")
                && !body.containsKey("minVote") && !body.containsKey("maxVote")
                && !body.containsKey("startDate") && !body.containsKey("endDate")
        ) {
            return ResponseEntity.ok().body(
                    new ResponseSuccess(movieService.getAllMovies()));

        }

        return filterService.filterMovies(
                Objects.equals(body.get("type").toString(), "OR") ? "OR" : "AND",
                body.get("collection") != null ? body.get("collection").toString() : "",
                body.get("genres") != null ? (List<String>) body.get("genres") : List.of(),
                body.get("keywords") != null ? (List<String>) body.get("keywords") : List.of(),
                body.get("minVote") != null ? Double.parseDouble( body.get("minVote").toString()) : 0,
                body.get("maxVote") != null ? Double.parseDouble( body.get("maxVote").toString()) : 10,
                body.get("startDate") != null ? (String) body.get("startDate") : "",
                body.get("endDate") != null ? (String) body.get("endDate") : "",
                body.get("page") != null ? Integer.parseInt(body.get("page").toString()) : 0,
                body.get("size") != null ? Integer.parseInt(body.get("size").toString()) : 10
        );
    }

    @GetMapping("/trending-day")
    public ResponseEntity<?> getMoviesTrendingDay(@RequestParam Map<String, String> param) {
        return moviesTrendingDayService.getAllMovies(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @GetMapping("/trending-week")
    public ResponseEntity<?> getMoviesTrendingWeek(@RequestParam Map<String, String> param) {
        return moviesTrendingWeekService.getAllMovies(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getMoviesPopular(@RequestParam Map<String, String> param) {
        return moviesPopularService.getAllMovies(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getMoviesUpcoming(@RequestParam Map<String, String> param) {
        return moviesUpcomingService.getAllMovies(
                param.get("ascending") != null && Boolean.parseBoolean(param.get("ascending")),
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

    @GetMapping("/lastest-trailers")
    public ResponseEntity<?> getMoviesLastestTrailers(@RequestParam Map<String, String> param) {
        return movieService.getMoviesSortedByLatestTrailer(
                param.get("page") != null ? Integer.parseInt(param.get("page")) : 0,
                param.get("size") != null ? Integer.parseInt(param.get("size")) : 10
        );
    }

}
