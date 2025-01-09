package com.example.movies_recommendation_API.movie_rating_list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "movie_rating_list") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRatingList {
    @Id
    private String _id;

    @Field("userId")
    private String userId;

    @Field("rating")
    private List<Map<String, Object>> ratingList;
}
