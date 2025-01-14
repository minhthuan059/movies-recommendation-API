package com.example.movies_recommendation_API.config;

import com.example.movies_recommendation_API.users.User;
import com.example.movies_recommendation_API.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AuthenticationProviderConfig implements AuthenticationProvider {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationProviderConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // Tìm User từ database
        User user = userService.getUserByUsernameAndGoogleIdIsEmpty(username);
        if (user == null) {
            throw new BadCredentialsException("Tài khoản không tồn tại.");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không chính xác.");
        }

        if(!user.isActive()) {
            throw new BadCredentialsException("Tài khoản chưa được xác thực.");
        }

        // Tạo đối tượng Authentication thành công
        return new UsernamePasswordAuthenticationToken(user, password, new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
