package com.example.webbansach_backend.dao;

import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.entity.Quyen;
import com.example.webbansach_backend.entity.Sach;
import com.example.webbansach_backend.entity.SachYeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "sach-yeu-thich")
public interface SachYeuThichRepository extends JpaRepository<SachYeuThich, Integer> {
    public SachYeuThich findSachYeuThichBySachAndNguoiDung(Sach sach, NguoiDung nguoiDung);
    public List<SachYeuThich> findSachYeuThichByNguoiDung(NguoiDung nguoiDung);
}
    