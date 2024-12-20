package com.example.movies_recommendation_API.Jwt;


import com.example.movies_recommendation_API.accounts.Account;
import com.example.movies_recommendation_API.accounts.AccountService;
import com.example.movies_recommendation_API.response.ResponseError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountService accountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;


        // Check if the header starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            if (authHeader.replaceAll("\\s+", "").equals("Bearer")) {
                handleException(response, "Token rỗng.");
                return;
            }

            token = authHeader.substring(7); // Extract token

            // Giải mã token để lấy username
            try {
                username = jwtService.extractUsername(token);
            } catch (RuntimeException e) {
                handleException(response, "Token không hợp lệ");
                return;
            }
        }



        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Account account = this.accountService.getAccountByUsername(username);

            if (jwtService.validateToken(token, account)) {
                // Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(account, null, new ArrayList<>());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Cập nhật SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            else {
                handleException(response, "Token hết hạn hoặc không hợp lệ");
                return;
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
        return;
    }


    private void handleException(HttpServletResponse response, String errorMessage) throws IOException {
        ResponseError error = ResponseError.builder()
                .status("error")
                .message(errorMessage)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        new ObjectMapper().writeValue(response.getWriter(), error);
        response.getWriter().flush();
        response.getWriter().close();
    }
}