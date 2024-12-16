package com.example.movies_recommendation_API.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Data                   // Tự động tạo getter, setter, toString, equals, hashCode
@NoArgsConstructor      // Constructor không tham số
@AllArgsConstructor     // Constructor đầy đủ tham số
@Builder
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Cột không cho phép null
    private String username;

    @Column(nullable = true) // Cột có thể null
    private String email;

    @Column(nullable = false) // Mật khẩu là bắt buộc
    private String password;

    @Column(nullable = true)
    private String googleId;

    @Column(updatable = false)
    private LocalDateTime createdAt; // Tự động gán giá trị mặc định

}
