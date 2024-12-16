package com.example.movies_recommendation_API.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseSuccess {
    private String status;
    private String token;
    private final Date timestamp = new Date();
    private String username;

    public LoginResponseSuccess(String status) {
        this.status = status;
    }
}

