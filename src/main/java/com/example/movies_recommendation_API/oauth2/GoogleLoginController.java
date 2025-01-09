package com.example.movies_recommendation_API.oauth2;

import com.example.movies_recommendation_API.Jwt.JwtService;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/google")
public class GoogleLoginController {

    @Autowired
    private GoogleAuthService googleAuthService;


    @PostMapping("")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        try {
            // Lấy tokenId từ request
            String tokenId = body.get("googleTokenId");
            if (tokenId == null || tokenId.isEmpty()) {
                ResponseError error = new ResponseError("Không nhận được token id.");
                return ResponseEntity.badRequest().body(error);
            }

            // Xác thực Google tokenId và tạo JWT
            return googleAuthService.authenticateGoogleToken(tokenId);

        } catch (Exception e) {
            ResponseError error = new ResponseError(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


}
