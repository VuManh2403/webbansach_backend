package com.example.webbansach_backend.service;

import com.example.webbansach_backend.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserSecurityService extends UserDetailsService {
    public User findByUsername(String username);
}
