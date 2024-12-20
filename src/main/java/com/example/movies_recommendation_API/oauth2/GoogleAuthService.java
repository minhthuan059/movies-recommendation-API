package com.example.movies_recommendation_API.oauth2;

import com.example.movies_recommendation_API.Jwt.JwtService;
import com.example.movies_recommendation_API.accounts.Account;
import com.example.movies_recommendation_API.accounts.AccountService;
import com.example.movies_recommendation_API.response.LoginResponseSuccess;
import com.example.movies_recommendation_API.response.ResponseError;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class GoogleAuthService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtService jwtService;

    // Khóa Client ID từ Google Console
    private final String CLIENT_ID;

    @Autowired
    public GoogleAuthService(OAuth2ClientProperties oAuth2ClientProperties) {
        this.CLIENT_ID = oAuth2ClientProperties.getRegistration()
                .get("google")
                .getClientId();
    }

    public ResponseEntity<?> authenticateGoogleToken(String tokenId) throws Exception {
        // Tạo GoogleIdTokenVerifier để xác minh tokenId
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(CLIENT_ID)).build();


        // Xác minh tokenId
        GoogleIdToken verifyIdToken = verifier.verify(tokenId);
        if (verifyIdToken == null) {
            ResponseError error = new ResponseError("error", "Id token không hợp lệ.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Lấy thông tin tài khoản Google
        GoogleIdToken.Payload payload = verifyIdToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // Tìm hoặc tạo User
        Account account = accountService.getAccountByGoogleId(googleId);
        if (account == null) {
            account = Account.builder()
                    .googleId(googleId)
                    .email(email)
                    .username(name)
                    .password("") // Không cần mật khẩu cho tài khoản Google
                    .createdAt(LocalDateTime.now())
                    .build();
            accountService.saveAccount(account);

        }

        String token = jwtService.generateToken(account);
        LoginResponseSuccess res = new LoginResponseSuccess("success");
        res.setToken(token);
        res.setUsername(account.getUsername());
        return ResponseEntity.ok().body(res);
    }
}
