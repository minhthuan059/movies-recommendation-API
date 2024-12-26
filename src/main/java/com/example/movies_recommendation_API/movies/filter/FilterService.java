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
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("genres.name").all(genres)), // Lọc theo genres
                    Aggregation.match(Criteria.where("vote_average").gte(minVoteAverage).lte(maxVoteAverage)), // Lọc theo vote_average
                    Aggregation.match(Criteria.where("release_date").gte(startDate).lte(endDate)), // Lọc theo release_date
                    Aggregation.lookup(trendingCollection, "id", "id", "movies"), // Join với movie_trending_day
                    Aggregation.unwind("movies"), // Làm phẳng kết quả sau join
                    Aggregation.match(Criteria.where("movies.id").exists(true)) // Kiểm tra phim có trong trending
            );

            // Bước 2: Thực hiện aggregation


            AggregationResults<Movie> results = mongoTemplate.aggregate(aggregation, "movies", Movie.class);
            List<Movie> movieList = results.getMappedResults();
            Integer totalElement = movieList.size();

            return ResponseEntity.ok().body(
                    new ResponseSuccess(new PageImpl<>(
                            movieList.subList(pageNumber*pageSize, Math.min((pageNumber + 1) * pageSize, movieList.size())),
                            pageable, totalElement))
            );
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
