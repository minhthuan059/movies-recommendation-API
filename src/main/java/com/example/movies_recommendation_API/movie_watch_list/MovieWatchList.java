package com.example.movies_recommendation_API.movie_watch_list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "movie_watch_list") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieWatchList {
    @Id
    private String _id;

    @Field("userId")
    private String userId;

    @Field("movieIds")
    private List<Integer> movieIds;
}
