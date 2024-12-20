package com.example.movies_recommendation_API.config;

import com.example.movies_recommendation_API.accounts.Account;
import com.example.movies_recommendation_API.accounts.AccountService;
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

    private AccountService accountService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationProviderConfig(AccountService accountService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // Tìm Account từ database
        Account account = accountService.getAccountByUsernameAndGoogleIdIsEmpty(username);
        if (account == null) {
            throw new BadCredentialsException("Tài khoản không tồn tại.");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không chính xác.");
        }

        // Tạo đối tượng Authentication thành công
        return new UsernamePasswordAuthenticationToken(account, password, new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
