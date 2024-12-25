package com.example.movies_recommendation_API.accounts;

import com.example.movies_recommendation_API.email.EmailService;
import com.example.movies_recommendation_API.email.OTP;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailService emailService;

    private final Map<String, OTP> otpStore = new ConcurrentHashMap<>();

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    public Account getAccountByUsername(String name) {
        return accountRepository.findOneByUsername(name);
    }

    public Account getAccountByUsernameAndGoogleId(String name, String googleId) {
        return accountRepository.findOneByUsernameAndGoogleId(name, googleId);
    }

    public Account getAccountByUsernameAndGoogleIdIsEmpty(String name) {
        return accountRepository.findOneByUsernameAndGoogleIdIsEmpty(name);
    }

    public Account getAccountByGoogleId(String googleId) {
        return accountRepository.findOneByGoogleId(googleId);
    }

    public ResponseEntity<?> createAccount(AccountCreateDTO accountCreateDTO) {
        try {
            if (accountRepository.findOneByUsernameAndGoogleIdIsEmpty(accountCreateDTO.getUsername()) != null) {
                ResponseError error = new ResponseError("error", "Username đã tồn tại.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            else {
                // Lọc trường hợp email đã có tài khoản thường.
                if (accountCreateDTO.getEmail() != null &&
                    accountRepository.findOneByEmailAndGoogleIdIsEmpty(accountCreateDTO.getEmail()) != null){
                    ResponseError error = new ResponseError("error", "Email đã được dùng đăng ký tài khoản thường.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                }
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(accountCreateDTO.getPassword());
                Account account = Account.builder()
                        .username(accountCreateDTO.getUsername())
                        .password(encodedPassword)
                        .email(accountCreateDTO.getEmail())
                        .createdAt(LocalDateTime.now())
                        .build();
                // Gọi repository để lưu vào database
                accountRepository.save(account);
                ResponseSuccess res = new ResponseSuccess("success");
                return ResponseEntity.status(HttpStatus.CREATED).body(res);
            }
        } catch (Exception e) {
            ResponseError error = new ResponseError("error", "Có lỗi trong quá trình tạo tài khoản.");
            return ResponseEntity.badRequest().body(error);
        }

    }

    public ResponseEntity<?> createAndSendOTP (String email) throws MessagingException {

        Account account = accountRepository.findOneByEmailAndGoogleIdIsEmpty(email);
        if (account == null) {
            ResponseError error = new ResponseError("error", "Email chưa đăng ký tài khoản thường.");
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
        ResponseSuccess res = new ResponseSuccess("success");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    public ResponseEntity<?> validateOtpAndResetPassword (String email, String otp, String newPassword) {
        OTP otpData = otpStore.get(email);
        System.out.println(otpStore.toString());

        if (otpData == null) {
            return ResponseEntity.badRequest().body(
                    new ResponseError("error", "OTP không tồn tại."));
        }

        if (!otpData.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body(
                    new ResponseError("error", "OTP không chính xác."));
        }

        if (otpData.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return ResponseEntity.badRequest().body(
                    new ResponseError("error", "OTP đã hết hạn."));
        }

        Account account = accountRepository.findOneByEmailAndGoogleIdIsEmpty(email);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newPassword);
        account.setPassword(encodedPassword);

        // Lưu lại người dùng đã thay đổi mật khẩu (không tạo tài khoản mới)
        accountRepository.save(account);

        // Xóa OTP sau khi sử dụng
        otpStore.remove(email);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseSuccess("success")
        );
    }

}
