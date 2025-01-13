package com.example.movies_recommendation_API.movies.filter;

import com.example.movies_recommendation_API.movies.Movie;
import com.example.movies_recommendation_API.movies.MovieRepository;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FilterService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MovieRepository movieRepository;

    public ResponseEntity<?> filterMovies(
            String type, String collectionName,
            List<String> genres, List<String> keywords,
            Double minVoteAverage, Double maxVoteAverage,
            String startDate, String endDate,
            Integer pageNumber, Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);


        // Xây dựng điều kiện chung
        Criteria criteria = new Criteria();

        if (Objects.equals(type, "AND")) {
            if (genres != null && !genres.isEmpty()) {
                criteria.and("genres.name").all(genres);
            }
            if (keywords != null && !keywords.isEmpty()) {
                criteria.and("keywords.name").all(keywords);
            }
        } else {
            if (genres != null && !genres.isEmpty()) {
                criteria.and("genres.name").in(genres);
            }
            if (keywords != null && !keywords.isEmpty()) {
                criteria.and("keywords.name").in(keywords);
            }
        }

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            criteria.and("release_date").gte(startDate).lte(endDate);
        } else if (!startDate.isEmpty()) {
            criteria.and("release_date").gte(startDate);
        } else if (!endDate.isEmpty()) {
            criteria.and("release_date").lte(endDate);
        }

        if (minVoteAverage >= 0 && maxVoteAverage <= 10) {
            criteria.and("vote_average").gte(minVoteAverage).lte(maxVoteAverage);
        } else if (minVoteAverage >= 0) {
            criteria.and("vote_average").gte(minVoteAverage);
        } else if (maxVoteAverage <= 10) {
            criteria.and("vote_average").lte(maxVoteAverage);
        }

        Aggregation aggregation = null;
        Aggregation countAggregation = null;
        if (!Objects.equals(collectionName, "")) {
            // Tạo aggregation để lọc theo các điều kiện và kết nối với collectionName
           aggregation = Aggregation.newAggregation(
                    // Kết hợp các điều kiện vào một match duy nhất
                    Aggregation.match(criteria),
                    Aggregation.lookup(collectionName, "id", "id", "trending_movies"),
                    Aggregation.match(Criteria.where("trending_movies").ne(Collections.emptyList())),

                    // Chỉ lấy các bản ghi trong phạm vi phân trang
                    Aggregation.skip((long) pageable.getOffset()),  // Bỏ qua các phần tử của các trang trước
                    Aggregation.limit(pageable.getPageSize())      // Giới hạn số lượng phần tử của trang hiện tại
            );

            countAggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),  // Sử dụng lại criteria đã có

                    Aggregation.lookup(collectionName, "id", "id", "trending_movies"),

                    Aggregation.match(Criteria.where("trending_movies").ne(Collections.emptyList())),

                    // Đếm tổng số phần tử sau khi lọc và join
                    Aggregation.count().as("total")
            );
        } else {
            aggregation = Aggregation.newAggregation(
                    // Kết hợp các điều kiện vào một match duy nhất
                    Aggregation.match(criteria),

                    // Chỉ lấy các bản ghi trong phạm vi phân trang
                    Aggregation.skip((long) pageable.getOffset()),  // Bỏ qua các phần tử của các trang trước
                    Aggregation.limit(pageable.getPageSize())      // Giới hạn số lượng phần tử của trang hiện tại
            );
            countAggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),  // Sử dụng lại criteria đã có

                    // Đếm tổng số phần tử sau khi lọc và join
                    Aggregation.count().as("total")
            );
        }



        // Thực hiện aggregation để lấy danh sách kết quả
        AggregationResults<Movie> results = mongoTemplate.aggregate(aggregation, "movies", Movie.class);


        // Thực hiện aggregation để lấy tổng số phần tử
        AggregationResults<Map> countResults = mongoTemplate.aggregate(countAggregation, "movies", Map.class);
        int totalElement = (countResults.getMappedResults().isEmpty()) ? 0 : (int) countResults.getMappedResults().get(0).get("total");

        // Trả về kết quả dưới dạng phân trang
        Page<Movie> page = new PageImpl<>(results.getMappedResults(), pageable, totalElement);
        return ResponseEntity.ok().body(new ResponseSuccess(page));


    }
}