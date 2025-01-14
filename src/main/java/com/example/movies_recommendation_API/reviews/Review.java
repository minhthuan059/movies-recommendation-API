package com.example.movies_recommendation_API.reviews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private String author;
    private AuthorDetails author_details;
    private String content;
    private String id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String url;
}



