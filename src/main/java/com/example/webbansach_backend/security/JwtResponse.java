package com.example.webbansach_backend.security;

// co the su dung lombok cx nhu lam binh thuong
public class JwtResponse {
    private final String jwt;

    public JwtResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
