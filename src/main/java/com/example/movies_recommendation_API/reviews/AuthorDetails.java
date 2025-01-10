package com.example.movies_recommendation_API.reviews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDetails {
    @Field("name")
    private String name;

    @Field("username")
    private String username;

    @Field("avatar_path")
    private String avatar_path;

    @Field("rating")
    private Double rating;
}