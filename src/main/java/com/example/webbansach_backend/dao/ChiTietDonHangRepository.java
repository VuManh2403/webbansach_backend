package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.ChiTietDonHang;
import com.example.webbansach_backend.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "chi-tiet-don-hang")
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {
    public List<ChiTietDonHang> findChiTietDonHangByDonHang(DonHang donHang);

}
