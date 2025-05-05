package com.example.webbansach_backend.service.capnhaphinhanh;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CapNhapHinhAnhServiceImpl implements CapNhapHinhAnhService {
    @Override
    public String capNhapHinhAnh(MultipartFile multipartFile, String name) {
        return "";
    }

    @Override
    public void xoaAnh(String imgUrl) {

    }
}
