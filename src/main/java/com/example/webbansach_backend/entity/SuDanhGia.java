package com.example.webbansach_backend.entity;

import lombok.Data;

@Data
public class SuDanhGia {
    private long maDanhGia;

    private float diemXepHang;

    private String nhanXet;

    private Sach sach;

    private NguoiDung nguoiDung;
}
