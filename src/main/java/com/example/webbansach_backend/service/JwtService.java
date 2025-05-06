package com.example.webbansach_backend.service;

import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.entity.Quyen;
import com.example.webbansach_backend.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtService {
    public static final String SERECT = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    @Autowired
    private UserService userService;

    // Tạo JWT dựa trên tên đang nhập
    // kiem tra quyen cua nguoi dung de di den trang admin hay trang chu
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        NguoiDung nguoiDung = userService.findByUsername(username);
        claims.put("maNguoiDung", nguoiDung.getMaNguoiDung());
        claims.put("avatar", nguoiDung.getAvatar());
        claims.put("ten", nguoiDung.getTen());
        claims.put("daKichHoat", nguoiDung.isDaKichHoat());
        List<Quyen> danhSachQuyen = nguoiDung.getDanhSachQuyen();
        if (danhSachQuyen.size() > 0) {
            for (Quyen quyen : danhSachQuyen) {
                if (quyen.getTenQuyen().equals("ADMIN")) {
                    claims.put("quyen", "ADMIN");
                    break;
                }
                if (quyen.getTenQuyen().equals("CUSTOMER")) {
                    claims.put("quyen", "CUSTOMER");
                    break;
                }
            }
        }


        return createToken(claims, username);
    }

    // Tạo JWT với các claim đã chọn
    private  String createToken(Map<String, Object> claims, String tenDangNhap){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(tenDangNhap)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+24*60*60*1000)) // JWT hết hạn sau 1 ngay
                .signWith(SignatureAlgorithm.HS256,getSigneKey())
                .compact();
    }

    // Lấy serect key
    private Key getSigneKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SERECT);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Trích xuất thông tin
    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSigneKey()).parseClaimsJws(token).getBody();
    }

    // Trích xuất thông tin cho 1 claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Kiểm tra thời gian hết hạn từ JWT
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // Kiểm tra tời gian hết hạn từ JWT
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Kiểm tra cái JWT đã hết hạn
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Kiểm tra tính hợp lệ
    public Boolean validateToken(String token, UserDetails userDetails){
        final String tenDangNhap = extractUsername(token);
        System.out.println(tenDangNhap);
        return (tenDangNhap.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }
}

