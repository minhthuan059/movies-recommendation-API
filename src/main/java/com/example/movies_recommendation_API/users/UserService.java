package com.example.movies_recommendation_API.users;

import com.example.movies_recommendation_API.email.EmailService;
import com.example.movies_recommendation_API.email.OTP;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private final Map<String, OTP> otpStore = new ConcurrentHashMap<>();


//    private final PasswordEncoder passwordEncoder;
//
//    public UserService(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUserByUsername(String name) {
        return userRepository.findOneByUsername(name);
    }

    public User getUserByUsernameAndGoogleId(String name, String googleId) {
        return userRepository.findOneByUsernameAndGoogleId(name, googleId);
    }

    public User getUserByUsernameAndGoogleIdIsEmpty(String name) {
        return userRepository.findOneByUsernameAndGoogleIdIsEmpty(name);
    }

    public User getUserByGoogleId(String googleId) {
        return userRepository.findOneByGoogleId(googleId);
    }

    public ResponseEntity<?> createUser(UserCreateDTO userCreateDTO) {
        try {
            if (userRepository.findOneByUsernameAndGoogleIdIsEmpty(userCreateDTO.getUsername()) != null) {
                ResponseError error = new ResponseError("Username đã tồn tại.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            } else {
                // Lọc trường hợp email đã có tài khoản thường.
                if (userCreateDTO.getEmail() != null &&
                        userRepository.findOneByEmailAndGoogleIdIsEmpty(userCreateDTO.getEmail()) != null) {
                    ResponseError error = new ResponseError("Email đã được dùng đăng ký tài khoản thường.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                }
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(userCreateDTO.getPassword());
                User user = User.builder()
                        .username(userCreateDTO.getUsername())
                        .password(encodedPassword)
                        .email(userCreateDTO.getEmail())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .isActive(false)
                        .build();
                // Gọi repository để lưu vào database
                userRepository.save(user);
                ResponseSuccess res = new ResponseSuccess();
                return ResponseEntity.status(HttpStatus.CREATED).body(res);
            }
        } catch (Exception e) {
            ResponseError error = new ResponseError("Có lỗi trong quá trình tạo tài khoản.");
            return ResponseEntity.badRequest().body(error);
        }

    }

    public ResponseEntity<?> createAndSendOTP(String email) throws MessagingException {

        User user = userRepository.findOneByEmailAndGoogleIdIsEmpty(email);
        if (user == null) {
            ResponseError error = new ResponseError("Email chưa đăng ký tài khoản thường.");
            return ResponseEntity.badRequest().body(error);
        }

        // Tạo OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        OTP otpData = new OTP(otp, LocalDateTime.now().plusMinutes(5));
        otpStore.put(email, otpData);


        // Gửi OTP qua email
        String emailContent = "<p>Mã OTP của bạn là:</p><h1>" + otp + "</h1>" +
                "<p>OTP có hiệu lực trong 5 phút.</p>";
        emailService.sendEmail(email, "OTP xác thực", emailContent);
        ResponseSuccess res = new ResponseSuccess();
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    public ResponseEntity<?> validateOtpAndResetPassword(String email, String otp, String newPassword) {
        OTP otpData = otpStore.get(email);

        if (otpData == null) {
            return ResponseEntity.badRequest().body(
                    new ResponseError("OTP không tồn tại."));
        }

        if (!otpData.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body(
                    new ResponseError("OTP không chính xác."));
        }

        if (otpData.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return ResponseEntity.badRequest().body(
                    new ResponseError("OTP đã hết hạn."));
        }

        User user = userRepository.findOneByEmailAndGoogleIdIsEmpty(email);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu lại người dùng đã thay đổi mật khẩu (không tạo tài khoản mới)
        userRepository.save(user);

        // Xóa OTP sau khi sử dụng
        otpStore.remove(email);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseSuccess()
        );
    }

    public ResponseEntity<?> validateOtpAndActiveUser(String email, String password, String otp) {
        OTP otpData = otpStore.get(email);

        if (otpData == null) {
            return ResponseEntity.badRequest().body(
                    new ResponseError("OTP không tồn tại.")
            );
        }

        if (!otpData.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body(
                    new ResponseError("OTP không chính xác.")
            );
        }

        if (otpData.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return ResponseEntity.badRequest().body(
                    new ResponseError("OTP đã hết hạn.")
            );
        }

        User user = userRepository.findOneByEmailAndGoogleIdIsEmpty(email);

        // Kiểm tra mật khẩu

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(
                    new ResponseError("Mật khẩu không chính xác.")
            );
        }

        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu lại người dùng đã thay đổi mật khẩu (không tạo tài khoản mới)
        userRepository.save(user);

        // Xóa OTP sau khi sử dụng
        otpStore.remove(email);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseSuccess()
        );
    }

}
