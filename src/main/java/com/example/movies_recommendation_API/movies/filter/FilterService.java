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
            String trendingCollection, List<String> genres,
            Double minVoteAverage, Double maxVoteAverage,
            String startDate, String endDate,
            Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        if (!Objects.equals(trendingCollection, "")) {
            // Xây dựng điều kiện chung
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where("vote_average").gte(minVoteAverage).lte(maxVoteAverage),
                    Criteria.where("release_date").gte(startDate).lte(endDate)
            );

            // Nếu genres không phải là danh sách rỗng, thêm điều kiện genres vào criteria
            if (genres != null && !genres.isEmpty()) {
                criteria.and("genres.name").all(genres);
            }

            // Tạo aggregation để lọc theo các điều kiện và kết nối với trendingCollection
            Aggregation aggregation = Aggregation.newAggregation(
                    // Kết hợp các điều kiện vào một match duy nhất
                    Aggregation.match(criteria),

                    // Kết nối với collection movie_trending_day
                    Aggregation.lookup(trendingCollection, "id", "id", "trending_movies"),

                    // Lọc chỉ những phim có mặt trong trending
                    Aggregation.match(Criteria.where("trending_movies").ne(Collections.emptyList())),

                    // Chỉ lấy các bản ghi trong phạm vi phân trang
                    Aggregation.skip((long) pageable.getOffset()),  // Bỏ qua các phần tử của các trang trước
                    Aggregation.limit(pageable.getPageSize())      // Giới hạn số lượng phần tử của trang hiện tại
            );

            // Thực hiện aggregation để lấy danh sách kết quả
            AggregationResults<Movie> results = mongoTemplate.aggregate(aggregation, "movies", Movie.class);

            // Tính tổng số phần tử (count)
            Aggregation countAggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),  // Sử dụng lại criteria đã có

                    Aggregation.lookup(trendingCollection, "id", "id", "trending_movies"),

                    Aggregation.match(Criteria.where("trending_movies").ne(Collections.emptyList())),

                    // Đếm tổng số phần tử sau khi lọc và join
                    Aggregation.count().as("total")
            );

            // Thực hiện aggregation để lấy tổng số phần tử
            AggregationResults<Map> countResults = mongoTemplate.aggregate(countAggregation, "movies", Map.class);
            int totalElement = (countResults.getMappedResults().isEmpty()) ? 0 : (int) countResults.getMappedResults().get(0).get("total");

            // Trả về kết quả dưới dạng phân trang
            Page<Movie> page = new PageImpl<>(results.getMappedResults(), pageable, totalElement);

            return ResponseEntity.ok().body(new ResponseSuccess(page));


        }
        else {
            Page<Movie> result = movieRepository.filterMovies(
                    genres,
                    minVoteAverage, maxVoteAverage,
                    startDate, endDate,
                    pageable
            );
            return ResponseEntity.ok().body(
                    new ResponseSuccess(result)
            );
        }

    }
}
