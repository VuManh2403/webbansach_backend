package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.Quyen;
import com.example.webbansach_backend.entity.SachYeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SachYeuThichRepository extends JpaRepository<SachYeuThich, Integer> {
    
}
    