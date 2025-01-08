package com.example.movies_recommendation_API.people;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "people") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class People {

    @Id
    private String _id; // ID kiểu String, tự động sinh bởi MongoDB

    @Field("tmdb_id")
    private Integer tmdb_id;

    @Field("adult")
    private boolean adult;

    @Field("also_known_as")
    private List<String> also_known_as;

    @Field("biography")
    private String biography;

    @Field("birthday")
    private String birthday;

    @Field("deathday")
    private String deathday;

    @Field("gender")
    private Integer gender;

    @Field("id")
    private Integer id;

    @Field("homepage")
    private String homepage;

    @Field("imdb_id")
    private String imdb_id;

    @Field("known_for_department")
    private  String known_for_department;

    @Field("name")
    private String name;

    @Field("place_of_birth")
    private String place_of_birth;

    @Field("popularity")
    private Double popularity;

    @Field("profile_path")
    private String profile_path;

    @Field("movie_credits")
    private Map<String, Object> movie_credits;

}



