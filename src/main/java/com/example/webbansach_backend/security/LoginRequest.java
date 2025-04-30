package com.example.webbansach_backend.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //  dung lombok get set tu dong
@AllArgsConstructor // tu tao Constructor
@NoArgsConstructor // tu tao Constructor rong
public class LoginRequest {
    private String username;
    private String password;
}
