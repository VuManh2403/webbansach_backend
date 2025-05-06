package com.example.webbansach_backend.security;

public class Endpoints {
    public static final String front_end_host = "http://localhost:3000";
    public static final String[] PUBLIC_GET = {
            "/sach",
            "/sach/**",
            "/the-loai/**",
            "/hinh-anh",
            "/hinh-anh/**",
            "/nguoi-dung/search/existsByTenDangNhap",
            "/nguoi-dung/search/existsByEmail",
            "/tai-khoan/kich-hoat",
            "/tai-khoan/kich-hoat/**",
//            "/gio-hang/**",
//            "/nguoi-dung/*/danh-sach-gio-hang",
//            "/don-hang/**",
//            "/chi-tiet-don-hang/**",
//            "/nguoi-dung/*/danh-sach-don-hang",
//            "/nguoi-dung/*/danh-sach-quyen",
//            "/nguoi-dung/*",
    };

    public static final String[] PUBLIC_POST = {
            "/tai-khoan/dang-ky",
            "/tai-khoan/dang-nhap",
    };

    public static final String[] ADMIN_ENDPOINT = {
            "/nguoi-dung",
            "/nguoi-dung/**",
            "/sach/**",
            "/sach",
    };

    public static final String[] PUBLIC_PUT = {

    };

    public static final String[] PUBLIC_DELETE = {

    };
}
