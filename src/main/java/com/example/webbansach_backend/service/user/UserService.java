package com.example.webbansach_backend.service.user;

import com.example.webbansach_backend.entity.NguoiDung;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    // nhan dien nguoi dung qua ten dang nhap
    public NguoiDung findByUsername(String tenDangNhap);

}
