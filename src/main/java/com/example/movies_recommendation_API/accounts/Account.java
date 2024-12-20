package com.example.movies_recommendation_API.accounts;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "accounts") // Tên collection trong MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    private String id; // ID kiểu String, tự động sinh bởi MongoDB

    @Field("username")
    private String username; // Không cần @Column, MongoDB không yêu cầu

    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("googleId")
    private String googleId;

    @Field("createAt")
    private LocalDateTime createdAt; // Dữ liệu dạng thời gian
}
