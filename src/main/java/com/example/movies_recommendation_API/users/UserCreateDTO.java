package com.example.movies_recommendation_API.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {

    @NotEmpty(message = "Tên người dùng không được bỏ trống.")
    @Size(min = 4, max = 50, message = "Tên người dùng phải có độ dài từ 4 đến 50 ký tự.")
    private String username;

    @NotEmpty(message = "Email không được bỏ trống.")
    @Email(message = "Email không hợp lệ.")
    private String email;

    @NotEmpty(message = "Mật khẩu không được bỏ trống.")
    @Size(min = 4, max = 255, message = "Mật khẩu phải có độ dài từ 4 đến 255 ký tự.")
    private String password;
}
