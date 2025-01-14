package com.example.movies_recommendation_API.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String _id; // ID kiểu String, tự động sinh bởi MongoDB

    @Field("username")
    private String username; // Không cần @Column, MongoDB không yêu cầu

    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("googleId")
    private String googleId;

    @Field("createdAt")
    private LocalDateTime createdAt; // Dữ liệu dạng thời gian

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    @Field("picture")
    private String picture;

    @Field("isActive")
    private boolean isActive;
}
