package com.example.webbansach_backend.entity;

import lombok.Data;

@Data
public class SachYeuThich {
    private int maSachYeuThich;

    private NguoiDung nguoiDung;

    private Sach sach;
}
