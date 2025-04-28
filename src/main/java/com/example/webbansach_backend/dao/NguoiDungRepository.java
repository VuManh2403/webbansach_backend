package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "nguoi-dung")
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    // kiem tra xem ten dang nhap ton tai hay khong
    public boolean existsByTenDangNhap(String tenDangNhap);

    // kiem tra email ton tai hay khong
    public boolean existsByEmail(String email);

    public NguoiDung findByTenDangNhap(String tenDangNhap);

    public NguoiDung findByEmail(String email);
}
    