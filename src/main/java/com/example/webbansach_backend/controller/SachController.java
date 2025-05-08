package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.sach.SachService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sach")
public class SachController {
    @Autowired
    private SachService sachService;

    @PostMapping(path = "/them-sach")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonData) {
        try {
            return sachService.save(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi r");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/cap-nhap-sach")
    public ResponseEntity<?> update(@RequestBody JsonNode jsonData) {
        try{
            return sachService.update(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi r");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/lay-tong-so-sach")
    public long getTotal() {
        return sachService.layTongSoSach();
    }
}
