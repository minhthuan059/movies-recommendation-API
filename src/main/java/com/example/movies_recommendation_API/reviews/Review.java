package com.example.movies_recommendation_API.reviews;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Field("author")
    private String author;

    @Field("author_details")
    private AuthorDetails author_details;

    @Field("content")
    private String content;

    @Field("id")
    private String id;

    @Field("created_at")
    private LocalDateTime created_at;

    @Field("updated_at")
    private LocalDateTime updated_at;

    @Field("url")
    private String url;

}



