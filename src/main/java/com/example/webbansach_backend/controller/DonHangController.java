package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.donhang.DonHangService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/danh-sach-don-hang")
public class DonHangController {
    @Autowired
    private DonHangService donHangService;
    @PostMapping("/them-don-hang")
    public ResponseEntity<?> themDonHang (@RequestBody JsonNode jsonData) {
        try{
            return donHangService.themDonHang(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/cap-nhap-don-hang") // update các trạng thái
    public ResponseEntity<?> capNhapDonHang (@RequestBody JsonNode jsonData) {
        try{
            return donHangService.capNhapDonHang(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/huy-don-hang") // khi thanh toán mà huỷ thanh toán
    public ResponseEntity<?> huyDonHang (@RequestBody JsonNode jsonNode) {
        try{
            return donHangService.huyDonHang(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
