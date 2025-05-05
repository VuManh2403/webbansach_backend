package com.example.webbansach_backend.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

// cấu hình Cloudinary – một dịch vụ lưu trữ & xử lý ảnh/video trên cloud

@Configuration
public class CloudinaryConfig {
    // cach nay se lam lo thong tin nen phải cau hinh trong file properties
//    private final String CLOUD_NAME
//    private final String API_KEY
//    private final String API_SECRET

    @Value("${cloud_name}")
    private String CLOUD_NAME;
    @Value("${api_key}")
    private String API_KEY;
    @Value("${api_secret}")
    private String API_SECRET;

    //    Config cloudinary (Nơi để chứa ảnh)
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        return new Cloudinary(config);
    }
}
