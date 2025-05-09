package com.example.webbansach_backend.service.giohang;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface GioHangService {
    public ResponseEntity<?> themGioHang(JsonNode jsonNode);
    public ResponseEntity<?> capNhapGioHang(JsonNode jsonNode);
}
