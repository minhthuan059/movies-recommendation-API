package com.example.movies_recommendation_API.movies;

import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

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

    public ResponseEntity<?> getListMovieByIds(
            List<String> ids,
            Integer pageNumber, Integer pageSize
            ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> movies = movieRepository.findByIdIn(ids, pageable);
        if (movies == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không tìm thấy phim có id yêu cầu")
            );
        }
        return ResponseEntity.ok().body(
                new ResponseSuccess(
                        movies
                )
        );
    }

    public ResponseEntity<?> getMoviesSortedByLatestTrailer(Integer pageNumber, Integer pageSize) {
        // Tính toán skip và limit cho phân trang
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int skip = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        // Tạo aggregation pipeline với phân trang
        Aggregation aggregation = Aggregation.newAggregation(
                // Lọc các movie có trailers không rỗng
                Aggregation.match(Criteria.where("trailers").ne(null)),

                // Trích xuất và so sánh ngày phát hành trailer mới nhất
                Aggregation.project("trailers", "_id", "title", "id", "tmdb_id")
                        .and("trailers.published_at").as("latestTrailerDate"),

                // Sắp xếp các movie theo trailer mới nhất
                Aggregation.sort(Sort.by(Sort.Order.desc("latestTrailerDate"))),

                // Phân trang - skip và limit
                Aggregation.skip(skip),
                Aggregation.limit(limit)
        );

        // Thực hiện aggregation
       AggregationResults<Movie> results = mongoTemplate.aggregate(aggregation, Movie.class, Movie.class);


        // Thực thi aggregation
     //   AggregationResults<Movie> results = mongoTemplate.aggregate(aggregation, Movie.class, Movie.class);


        // Tổng số phần tử trong collection
        long totalElements = mongoTemplate.count(new Query(), Movie.class);

        // Trả về kết quả phân trang
        PageImpl<Movie> page = new PageImpl<>(results.getMappedResults(), pageable, totalElements);

        // Trả về kết quả
        return ResponseEntity.ok(page);
    }

}
