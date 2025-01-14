package com.example.movies_recommendation_API.movie_rating_list;

import com.example.movies_recommendation_API.movies.Movie;
import com.example.movies_recommendation_API.movies.MovieRepository;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import com.example.movies_recommendation_API.reviews.ReviewService;
import com.example.movies_recommendation_API.users.User;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieRatingListService {

    @Autowired
    private MovieRatingListRepository movieRatingListRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private ReviewService reviewService;

    @Autowired
    public void ReviewService(@Lazy ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    public void addRatingToMovieRatingList(
        Integer movieId, Double rating
    ){
        Movie movie = movieRepository.findOneById(movieId);
        if (movie == null) {
           return;
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieRatingList movieRatingList = movieRatingListRepository.findByUserId(user.get_id());
        if (movieRatingList == null) {
            movieRatingList = MovieRatingList.builder()
                    .userId(user.get_id())
                    .ratings(List.of(Map.of("movieId", movieId, "rating", rating)))
                    .build();
        } else {
            if (!movieRatingList.getRatings().stream()
                    .anyMatch(element -> movieId.equals(element.get("movieId")))) {
                movieRatingList.getRatings().add(Map.of("movieId", movieId, "rating", rating));
            } else {
                return;
            }
        }

        movieRatingListRepository.save(movieRatingList);
    }


    public ResponseEntity<?> getMoviesByUserId(
            Integer pageNumber, Integer pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Giai đoạn Aggregation chính
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(user.get_id())), // Lọc userId
                Aggregation.unwind("ratings"), // Tách từng phần tử trong ratings
                Aggregation.lookup("movies", "ratings.movieId", "id", "movie_details"), // Join với movies
                Aggregation.unwind("movie_details"), // Tách mảng movie_details
                Aggregation.project() // Lấy toàn bộ trường và rating
                        .and("movie_details").as("movie")
                        .and("ratings.rating").as("rating"),
                Aggregation.skip((long) pageable.getOffset()), // Skip các bản ghi
                Aggregation.limit(pageable.getPageSize()) // Giới hạn số lượng bản ghi trả về
        );

        // Giai đoạn đếm tổng số bản ghi
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(user.get_id())),
                Aggregation.unwind("ratings"),
                Aggregation.lookup("movies", "ratings.movieId", "id", "movie_details"),
                Aggregation.unwind("movie_details"),
                Aggregation.count().as("total")
        );

        // Thực thi Aggregation chính để lấy dữ liệu
        List<Document> results = mongoTemplate.aggregate(aggregation, "movie_rating_list", Document.class).getMappedResults();

        // Thực thi Aggregation đếm để lấy tổng số bản ghi
        Document countResult = mongoTemplate.aggregate(countAggregation, "movie_rating_list", Document.class).getUniqueMappedResult();
        Integer total = countResult != null ? countResult.get("total", Integer.class) : 0;

        // Chuyển đổi kết quả sang List<Map<String, Object>>
        List<Map<String, Object>> movies = results.stream()
                .map(Document::entrySet)
                .map(entries -> {
                    Map<String, Object> map = new HashMap<>();
                    entries.forEach(entry -> map.put(entry.getKey(), entry.getValue()));
                    return map;
                })
                .collect(Collectors.toList());
        // Trả về kết quả dưới dạng phân trang
        PageImpl<Map<String, Object>> pageResult = new PageImpl<>(movies, pageable, total);

        // Trả về kết quả dưới dạng Page
        return ResponseEntity.ok().body(
                new ResponseSuccess(
                    pageResult
                )
        );
    }

    public void deleteMovieInRatingList (Integer movieId) {
        Movie movie = movieRepository.findOneById(movieId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieRatingList movieRatingList = movieRatingListRepository.findByUserId(user.get_id());
        if (movieRatingList == null) {
            return;
        } else {
            List<Integer> movieIds = movieRatingList.getRatings().stream()
                    .filter(rating -> rating.containsKey("movieId"))  // Lọc ra phần tử chứa movieId
                    .map(rating -> (Integer) rating.get("movieId"))  // Lấy giá trị movieId và ép kiểu
                    .collect(Collectors.toList());
            if (movieIds.contains(movieId)) {
                movieRatingList.getRatings().removeIf(
                        rating -> Integer.parseInt(rating.get("movieId").toString()) == movieId);
                if (movieRatingList.getRatings().isEmpty()) {
                    movieRatingListRepository.delete(movieRatingList);
                } else {
                    movieRatingListRepository.save(movieRatingList);
                }
            }
        }
    }

    public ResponseEntity<?> deleteAllMovieToRatingList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MovieRatingList movieRatingList = movieRatingListRepository.findByUserId(user.get_id());
        if (movieRatingList == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Danh sách xem trống.")
            );
        }

        for (Map<String, Object> rating : movieRatingList.getRatings()) {
            reviewService.deleteReviewInMovieWithOutRattingList(Integer.parseInt(rating.get("movieId").toString()));
        }

        movieRatingListRepository.deleteAll();
        return ResponseEntity.ok().body(
                new ResponseSuccess()
        );
    }
}
