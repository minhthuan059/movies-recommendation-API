package com.example.movies_recommendation_API.movies_favorite_list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "movies_favorite_list") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoviesFavoriteList {
    @Id
    private String _id;

    @Field("userId")
    private String userId;

    @Field("movieIds")
    private List<Integer> movieIds;
}
