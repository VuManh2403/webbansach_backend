package com.example.webbansach_backend.entity;

import lombok.Data;

import java.util.List;

@Data
public class Sach {
    private int maSach;

    private String tenSach;

    private String tenTacGia;

    // ISBN ma so xuat ban the gioi
    private String ISBN;

    private String moTa;

    private double giaNiemYet;

    private double giaBan;

    private int soLuong;

    private Double trungBinhXepHang;

    List<TheLoai> danhSachTheLoai;

    List<HinhAnh> danhSachHinhAnh;

    List<SuDanhGia> danhSachSuDanhGia;

    List<ChiTietDonHang> danhSachChiTietDonHang;

    List<SachYeuThich> danhSachSachYeuThich;
}
