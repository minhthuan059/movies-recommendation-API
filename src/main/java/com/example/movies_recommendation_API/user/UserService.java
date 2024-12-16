package com.example.movies_recommendation_API.user;

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
public class UserService {

    @Autowired
    private UserRepository userRepository;


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

    public User getUserByUsernameAndGoogleIdIsNull(String name) {
        return userRepository.findOneByUsernameAndGoogleIdIsNull(name);
    }

    public User getUserByGoogleId(String googleId) {
        return userRepository.findOneByGoogleId(googleId);
    }

    public ResponseEntity<?> createUser(UserCreateDTO userCreateDTO) {
        try {
            if (userRepository.findOneByUsernameAndGoogleIdIsNull(userCreateDTO.getUsername()) != null) {
                ResponseError error = new ResponseError("error", "Username đã tồn tại.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            else {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(userCreateDTO.getPassword());
                User user = User.builder()
                        .username(userCreateDTO.getUsername())
                        .password(encodedPassword)
                        .email(userCreateDTO.getEmail())
                        .createdAt(LocalDateTime.now())
                        .build();
                // Gọi repository để lưu vào database
                userRepository.save(user);
                ResponseSuccess res = new ResponseSuccess("success");
                return ResponseEntity.status(HttpStatus.CREATED).body(res);
            }
        } catch (Exception e) {
            ResponseError error = new ResponseError("error", "Có lỗi trong quá trình tạo tài khoản.");
            return ResponseEntity.badRequest().body(error);
        }

    }

}
