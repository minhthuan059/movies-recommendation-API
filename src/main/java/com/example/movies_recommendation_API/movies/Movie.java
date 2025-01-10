package com.example.movies_recommendation_API.movies;

import com.example.movies_recommendation_API.reviews.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "movies") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    private String _id; // ID kiểu String, tự động sinh bởi MongoDB

    @Field("tmdb_id")
    private Integer tmdb_id;

    @Field("adult")
    private boolean adult;

    @Field("backdrop_path")
    private String backdropPath;

    @Field("belongs_to_collection")
    private Map<String, Object> belongs_to_collection;

    @Field("budget")
    private Integer budget;

    @Field("categories")
    private List<String> categories;

    @Field("genres")
    private List<Map<String, Object>> genres;

    @Field("homepage")
    private String homepage;

    @Field("id")
    private Integer id;

    @Field("imdb_id")
    private String imdb_id;

    @Field("origin_country")
    private List<String> origin_country;

    @Field("original_language")
    private String original_language;

    @Field("original_title")
    private String original_title;

    @Field("overview")
    private String overview;

    @Field("popularity")
    private Double popularity;

    @Field("poster_path")
    private String poster_path;

    @Field("production_companies")
    private List<Map<String, Object>> production_companies;

    @Field("production_countries")
    private List<Map<String, Object>> production_countries;

    @Field("release_date")
    private String release_date;

    @Field("revenue")
    private Long revenue;

    @Field("runtime")
    private Integer runtime;

    @Field("spoken_languages")
    private List<Map<String, String>> spoken_languages;

    @Field("status")
    private String status;

    @Field("tagline")
    private String tagline;

    @Field("title")
    private String title;

    @Field("video")
    private boolean video;

    @Field("vote_average")
    private Double voteAverage;

    @Field("vote_count")
    private Integer voteCount;

    @Field("credits")
    private Map<String, Object> credits;

    @Field("trailers")
    private List<Map<String, Object>> trailers;

    @Field("similar_movies")
    private List<Map<String, Object>> similar_movies;

    @Field("keywords")
    private List<Map<String, Object>> keywords;

    @Field("reviews")
    private List<Review> reviews;
}

