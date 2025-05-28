package com.example.webbansach_backend.service.JWT;

import com.example.webbansach_backend.service.UserSecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserSecurityService userSecurityService;

    // kiểm tra và xác thực người dùng dựa trên JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            // Lấy header "Authorization" từ HTTP request. Đây thường là nơi chứa JWT token.
            String authHeader = request.getHeader("Authorization");

            String token = null;
            String username = null;

            // Kiểm tra xem header có tồn tại và có bắt đầu bằng "Bearer " không (chuẩn theo RFC 6750).
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // Cắt phần "Bearer " ra để chỉ lấy token thực sự.
                username = jwtService.extractUsername(token); // lay username tu token de xu ly tiep
            }

            // Kiểm tra nếu username hợp lệ và hiện tại chưa có thông tin xác thực nào trong context.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                //Lấy thông tin người dùng từ hệ thống (thường từ cơ sở dữ liệu).
                UserDetails userDetails = userSecurityService.loadUserByUsername(username);

                // Kiểm tra token có hợp lệ và đúng với người dùng hay không.
                if (jwtService.validateToken(token, userDetails)) {
                    //Tạo đối tượng xác thực từ thông tin người dùng.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Đặt thông tin xác thực vào Spring Security Context, cho phép tiếp tục xử lý như người dùng đã đăng nhập.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
