package com.example.movies_recommendation_API.oauth2;

import com.example.movies_recommendation_API.Jwt.JwtService;
import com.example.movies_recommendation_API.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleLoginController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtService jwtService;


}
