package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dao.NguoiDungRepository;
import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.entity.ThongBao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TaiKhoanService {
    // quan ly tai khoan nguoi dung
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Autowired tu dong tao doi tuong
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public ResponseEntity<?> dangKyNguoiDung(NguoiDung nguoiDung) {
        // Kiểm tra tên đăng nhập đã tồn tại chưa?
        if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
            return ResponseEntity.badRequest().body(new ThongBao("Tên đăng nhập đã tồn tại."));
        }

        // Kiểm tra email đã tồn tại chưa?
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            return ResponseEntity.badRequest().body(new ThongBao("Email đã tồn tại."));
        }

        // Mã hóa mật khẩu
        String encryptPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
        nguoiDung.setMatKhau(encryptPassword);

        // Lưu người dùng người dùng vào DB
        NguoiDung nguoiDung_daDangKy = nguoiDungRepository.save(nguoiDung);
        return ResponseEntity.ok("Đăng ký thành công");
    }
}
