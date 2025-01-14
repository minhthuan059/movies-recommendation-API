package com.example.movies_recommendation_API.oauth2;

import com.example.movies_recommendation_API.Jwt.JwtService;
import com.example.movies_recommendation_API.response.LoginResponseSuccess;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.users.User;
import com.example.movies_recommendation_API.users.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    // Khóa Client ID từ Google Console
    private final String CLIENT_ID;

    public GoogleAuthService(OAuth2ClientProperties oAuth2ClientProperties) {
        this.CLIENT_ID = oAuth2ClientProperties.getRegistration()
                .get("google")
                .getClientId();
    }

    public ResponseEntity<?> authenticateGoogleToken(String tokenId) throws Exception {
        // Tạo GoogleIdTokenVerifier để xác minh tokenId
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(tokenId);
        if (idToken == null) {
            ResponseError error = new ResponseError("Id token không hợp lệ.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Lấy thông tin tài khoản Google
        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String avatar = (String) payload.get("picture");

        // Tìm hoặc tạo User
        User user = userService.getUserByGoogleId(googleId);
        if (user == null) {
            user = User.builder()
                    .googleId(googleId)
                    .email(email)
                    .username(name)
                    .password("") // Không cần mật khẩu cho tài khoản Google
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .picture(avatar)
                    .isActive(true)
                    .build();
            userService.saveUser(user);
        }

        String token = jwtService.generateToken(user);
        LoginResponseSuccess res = new LoginResponseSuccess();
        res.setToken(token);
        res.setUsername(user.getUsername());
        return ResponseEntity.ok().body(res);
    }
}
