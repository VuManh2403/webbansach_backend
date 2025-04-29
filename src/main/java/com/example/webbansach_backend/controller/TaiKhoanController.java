package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from 'http://localhost:3000'
@RequestMapping("/tai-khoan")
public class TaiKhoanController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKyNguoiDung(@Validated @RequestBody NguoiDung nguoiDung){
        ResponseEntity<?> response = taiKhoanService.dangKyNguoiDung(nguoiDung);
        return response;
    }
    // nhung gi dc them vao csdl phai dung post chu ko dc dung get

    @GetMapping("/kich-hoat")
    // GetMapping de cho nguoi dung kich vao duong link chu ko de dien form nhanh gon hon
    public ResponseEntity<?> activeAccount(@RequestParam String email, @RequestParam String maKichHoat) {
        // RequestParam de thong tin tren url
        ResponseEntity<?> response = taiKhoanService.kichHoatTaiKhoan(email, maKichHoat);
        return response;
    }
}

