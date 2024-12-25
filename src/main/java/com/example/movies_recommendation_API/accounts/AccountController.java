package com.example.movies_recommendation_API.accounts;

import com.example.movies_recommendation_API.Jwt.JwtService;
import com.example.movies_recommendation_API.oauth2.GoogleAuthService;
import com.example.movies_recommendation_API.response.LoginResponseSuccess;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GoogleAuthService googleAuthService;


    @GetMapping("")
    public ResponseEntity<?> getAccounts() {
        return ResponseEntity.ok().body(accountService.getAllAccounts());
    }


    @PostMapping("/register")
    public ResponseEntity<?> postRegisterAccount(@Valid @RequestBody AccountCreateDTO account) {
        return accountService.createAccount(account);
    }

    @PostMapping("/login")
    public ResponseEntity<?> postLogin(@RequestBody Account loginRequest) {
        try {
            // Xác thực thông tin đăng nhập
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Tải thông tin người dùng
            Account account = accountService.getAccountByUsername(loginRequest.getUsername());

            // Tạo JWT token
            String token = jwtService.generateToken(account);

            LoginResponseSuccess res = new LoginResponseSuccess("success");
            res.setUsername(account.getUsername());
            res.setToken(token);
            return ResponseEntity.ok().body(res);
        } catch (AuthenticationException e) {
            ResponseError error = new ResponseError("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        try {
            // Lấy tokenId từ request
            String tokenId = body.get("googleTokenId");
            if (tokenId == null || tokenId.isEmpty()) {
                ResponseError error = new ResponseError("error", "Không nhận được token id.");
                return ResponseEntity.badRequest().body(error);
            }

            // Xác thực Google tokenId và tạo JWT
            return googleAuthService.authenticateGoogleToken(tokenId);

        } catch (Exception e) {
            ResponseError error = new ResponseError("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            // Lấy thông tin từ SecurityContext
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ResponseSuccess res = new ResponseSuccess("success", account);

            // Trả về thông tin người dùng
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseError error = new ResponseError("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> postRequestSendOTP (@RequestBody Map<String, String> body) throws MessagingException {
        String email = body.get("email");
        return accountService.createAndSendOTP(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?>  postRequestChangePassword (@RequestBody Map<String, String> body) {
        return accountService.validateOtpAndResetPassword(body.get("email"), body.get("otp"), body.get("password"));
    }


}
