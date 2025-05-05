package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.DonHang;
import com.example.webbansach_backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "don-hang")
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {
    // order o day la chi cau lenh sap xep
    public DonHang findFirstByNguoiDungOrderByMaDonHangDesc(NguoiDung nguoiDung);
}
    