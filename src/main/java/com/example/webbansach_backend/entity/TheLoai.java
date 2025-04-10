package com.example.webbansach_backend.entity;

import lombok.Data;

import java.util.List;

@Data
public class TheLoai {
    private int maTheLoai;

    private String tenTheLoai;

    private List<Sach> danhSachQuyenSach;
}
