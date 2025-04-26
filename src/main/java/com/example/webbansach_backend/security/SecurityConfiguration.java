package com.example.webbansach_backend.security;

import com.example.webbansach_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    //  giup ma hoa mat khau
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    // Bean ko di dc voi autowired
//    @Autowired
    public DaoAuthenticationProvider authenticationProvider(UserService userService){
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(userService);
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }

    // phan quyen truy cap
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                config -> config
                        .requestMatchers(HttpMethod.GET, "/sach").permitAll() // khong can dang nhap co the goi dc
                        .requestMatchers(HttpMethod.GET, "/sach/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/hinh-anh").permitAll()
                        .requestMatchers(HttpMethod.GET, "/hinh-anh/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/nguoi-dung").hasAnyAuthority("ADMIN", "STAFF") // quyen admin, staff co the goi dc
                        .requestMatchers(HttpMethod.GET, "/nguoi-dung/search/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tai-khoan/dang-ky").permitAll()
        );
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }
}
