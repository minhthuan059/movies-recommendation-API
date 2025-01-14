package com.example.movies_recommendation_API.reviews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDetails {
    private String name;
    private String username;
    private String avatar_path;
    private Double rating;
}