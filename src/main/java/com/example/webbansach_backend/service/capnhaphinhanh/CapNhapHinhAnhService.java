package com.example.webbansach_backend.service.capnhaphinhanh;

import org.springframework.web.multipart.MultipartFile;

public interface CapNhapHinhAnhService {
    String capNhapHinhAnh(MultipartFile multipartFile, String name);
    void xoaAnh(String imgUrl);
}
