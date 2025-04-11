package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.SuDanhGia;
import com.example.webbansach_backend.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Integer> {
    
}
    