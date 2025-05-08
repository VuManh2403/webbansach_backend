package com.example.webbansach_backend.security;

public class Endpoints {
    public static final String front_end_host = "http://localhost:3000";
    public static final String[] PUBLIC_GET = {
            "/sach",
            "/sach/**",
            "/the-loai/**",
            "/hinh-anh",
            "/hinh-anh/**",
            "/su-danh-gia/**",
            "/nguoi-dung/search/existsByTenDangNhap",
            "/nguoi-dung/search/existsByEmail",
            "/tai-khoan/kich-hoat",
            "/tai-khoan/kich-hoat/**",
            "/gio-hang/**",
            "/nguoi-dung/*/danhSachGioHang",
//            "/don-hang/**",
//            "/chi-tiet-don-hang/**",
            "/nguoi-dung/*/danhSachDonHang",
            "/nguoi-dung/*/danhSachQuyen",
            "/nguoi-dung/*",
            "/nguoi-dung/*/danhSachSachYeuThich",
            "/yeu-thich/lay-sach-yeu-thich/**",
            "/sach-yeu-thich/*/sach",
    };

    public static final String[] PUBLIC_POST = {
            "/tai-khoan/dang-ky",
            "/tai-khoan/dang-nhap",
            "/danh-gia/them-danh-gia/**",
            "/danh-gia/lay-danh-gia/**",
            "/yeu-thich/them-sach-yeu-thich",
    };

    public static final String[] ADMIN_ENDPOINT = {
            "/nguoi-dung",
            "/nguoi-dung/**",
            "/sach/**",
            "/sach",
            "/quyen/**",
            "/danh-gia/**",
            "/tai-khoan/them-nguoi-dung/**",
            "/yeu-thich/**",
            "/sach-yeu-thich/**",
    };

    public static final String[] PUBLIC_PUT = {
            "/danh-gia/cap-nhap-danh-gia",
            "/nguoi-dung/**",
            "/tai-khoan/cap-nhap-profile",
            "/tai-khoan/thay-doi-mat-khau",
            "/tai-khoan/forgot-password",
            "/tai-khoan/thay-doi-avatar",
            "/user/quen-mat-khau",
    };

    public static final String[] PUBLIC_DELETE = {
            "/yeu-thich/xoa-sach-yeu-thich",
    };
}
