package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.security.JwtResponse;
import com.example.webbansach_backend.security.LoginRequest;
import com.example.webbansach_backend.service.nguoidung.NguoiDungServiceImpl;
import com.example.webbansach_backend.service.user.UserService;
import com.example.webbansach_backend.service.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/tai-khoan")
public class TaiKhoanController {

    @Autowired
    private NguoiDungServiceImpl nguoiDungServiceImpl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    // Allow requests from 'http://localhost:3000'
    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKyNguoiDung(@Validated @RequestBody NguoiDung nguoiDung){
        ResponseEntity<?> response = nguoiDungServiceImpl.dangKyNguoiDung(nguoiDung);
        return response;
    }

    @GetMapping("/kich-hoat")
    public ResponseEntity<?> kichHoatTaiKhoan(@RequestParam String email, @RequestParam String maKichHoat){
        ResponseEntity<?> response = nguoiDungServiceImpl.kichHoatTaiKhoan(email, maKichHoat);
        return response;
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhap(@RequestBody LoginRequest loginRequest){
        // Xác thực người dùng bằng tên đăng nhập và mật khẩu
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            // Nếu xác thực thành công, tạo token JWT
            if(authentication.isAuthenticated()){
                final String jwt = jwtService.generateToken(loginRequest.getUsername());
                return ResponseEntity.ok(new JwtResponse(jwt));
            }
        }catch (AuthenticationException e){
            // Xác thực không thành công, trả về lỗi hoặc thông báo
            return ResponseEntity.badRequest().body("Tên đăng nhập hặc mật khẩu không chính xác.");
        }
        return ResponseEntity.badRequest().body("Xác thực không thành công.");
    }

    @PutMapping(path = "/quen-mat-khau")
    public ResponseEntity<?> forgotPassword(@RequestBody JsonNode jsonNode) {
        try{
            return nguoiDungServiceImpl.quyenMatKhau(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/thay-doi-mat-khau")
    public ResponseEntity<?> changePassword(@RequestBody JsonNode jsonData) {
        System.out.println(jsonData);
        try{
            return nguoiDungServiceImpl.thayDoiMatKhau(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/thay-doi-avatar")
    public ResponseEntity<?> changeAvatar(@RequestBody JsonNode jsonData) {
        try{
            return nguoiDungServiceImpl.thayDoiAvatar(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/cap-nhap-profile")
    public ResponseEntity<?> updateProfile(@RequestBody JsonNode jsonData) {
        try{
            return nguoiDungServiceImpl.capNhapProfile(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/them-nguoi-dung")
    public ResponseEntity<?> save (@RequestBody JsonNode jsonData) {
        try{
            return nguoiDungServiceImpl.themNguoiDung(jsonData, "thêm");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}

