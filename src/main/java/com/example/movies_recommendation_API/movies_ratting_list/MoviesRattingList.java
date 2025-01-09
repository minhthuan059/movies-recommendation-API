package com.example.movies_recommendation_API.movies_ratting_list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "movies_ratting_list") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoviesRattingList {
    @Id
    private String _id;

    @Field("userId")
    private String userId;

    @Field("movieId")
    private String movieId;

    @Field("ratting")
    private Double ratting;
}
