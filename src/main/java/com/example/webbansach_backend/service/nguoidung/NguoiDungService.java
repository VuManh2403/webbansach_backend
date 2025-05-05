package com.example.webbansach_backend.service.nguoidung;

import com.example.webbansach_backend.entity.NguoiDung;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface NguoiDungService {
    public ResponseEntity<?> dangKyNguoiDung(NguoiDung nguoiDung);
    public ResponseEntity<?> capNhap(JsonNode nguoiDungJson, String option);
    public ResponseEntity<?> xoa(int id);
    public ResponseEntity<?> thayDoiMatKhau(JsonNode nguoiDungJson);
    public ResponseEntity<?> thayDoiAvatar(JsonNode nguoiDungJson);
    public ResponseEntity<?> capNhapProfile(JsonNode nguoiDungJson);
    public ResponseEntity<?> quyenMatKhau(JsonNode jsonNode);
}
