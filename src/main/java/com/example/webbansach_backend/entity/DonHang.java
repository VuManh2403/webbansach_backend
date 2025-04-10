package com.example.webbansach_backend.entity;

import lombok.Data;

import java.util.List;

@Data
public class DonHang {
    private int maDonHang;

    private Data ngayTao;

    private String diaChiMuaHang;

    private String diaChiNhanHang;

    private double tongTienSanPham;

    private double chiPhiGiaoHang;

    private double chiPhiThanhToan;

    private double tongTien;

    private List<ChiTietDonHang> danhSachChiTietDonHang;

    private NguoiDung nguoiDung;

    private HinhThucThanhToan hinhThucThanhToan;

    private HinhThucGiaoHang hinhThucGiaoHang;
}
