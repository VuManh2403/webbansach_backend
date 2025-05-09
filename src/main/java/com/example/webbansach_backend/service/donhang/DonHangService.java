package com.example.webbansach_backend.service.donhang;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface DonHangService {
    public ResponseEntity<?> themDonHang(JsonNode jsonData);
    public ResponseEntity<?> capNhapDonHang(JsonNode jsonData);
    public ResponseEntity<?> huyDonHang (JsonNode jsonData);
}
