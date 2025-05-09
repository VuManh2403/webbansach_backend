package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.giohang.GioHangService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chi-tiet-gio-hang")
public class GioHangController {
    @Autowired
    private GioHangService gioHangService;

    @PostMapping("/them-gio-hang")
    public ResponseEntity<?> themGioHang(@RequestBody JsonNode jsonData) {
        try{
            return gioHangService.themGioHang(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/cap-nhap-gio-hang")
    private ResponseEntity<?> update(@RequestBody JsonNode jsonData) {
        try{
            gioHangService.capNhapGioHang(jsonData);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
