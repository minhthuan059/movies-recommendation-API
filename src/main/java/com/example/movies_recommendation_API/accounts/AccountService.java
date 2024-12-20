package com.example.movies_recommendation_API.accounts;

import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;


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

}
