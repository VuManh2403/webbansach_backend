package com.example.webbansach_backend.service.nguoidung;

import com.example.webbansach_backend.dao.NguoiDungRepository;
import com.example.webbansach_backend.dao.QuyenRepository;
import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.entity.Quyen;
import com.example.webbansach_backend.entity.ThongBao;
import com.example.webbansach_backend.security.JwtResponse;
import com.example.webbansach_backend.service.JwtService;
import com.example.webbansach_backend.service.capnhaphinhanh.CapNhapHinhAnhService;
import com.example.webbansach_backend.service.email.EmailService;
import com.example.webbansach_backend.service.util.Base64ToMultipartFileConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NguoiDungServiceImpl implements NguoiDungService {
    // quan ly tai khoan nguoi dung
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Autowired tu dong tao doi tuong
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private QuyenRepository quyenRepository;
    @Autowired
    private CapNhapHinhAnhService capNhapHinhAnhService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtService jwtService;

    private final ObjectMapper objectMapper;

    public NguoiDungServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<?> dangKyNguoiDung(NguoiDung nguoiDung) {
        // Kiểm tra tên đăng nhập đã tồn tại chưa?
        if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
            return ResponseEntity.badRequest().body(new ThongBao("Tên đăng nhập đã tồn tại."));
        }

        // Kiểm tra email đã tồn tại chưa?
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            return ResponseEntity.badRequest().body(new ThongBao("Email đã tồn tại."));
        }

        // Mã hóa mật khẩu
        String encryptPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
        nguoiDung.setMatKhau(encryptPassword);

        // Gán và gửi thông tin kích hoạt
        nguoiDung.setMaKichHoat(taoMaKichHoat());
        nguoiDung.setDaKichHoat(false); // chua kich hoat nen de false

        // Lưu người dùng người dùng vào DB
        NguoiDung nguoiDung_daDangKy = nguoiDungRepository.save(nguoiDung);

        // Gửi email cho người dùng để họ kích hoạt
        guiEmailKichHoat(nguoiDung.getEmail(), nguoiDung.getMaKichHoat());
        return ResponseEntity.ok("Đăng ký thành công");
    }

    private String taoMaKichHoat(){
        // Tạo mã ngẫu nhiên
        return UUID.randomUUID().toString();
    }

    private void guiEmailKichHoat(String email, String maKichHoat){
        String subject = "Kích hoạt tài khoản của bạn tại WebBanSach";
        String text = "Vui lòng sử dụng mã sau để kich hoạt cho tài khoản <"+email+">:<html><body><br/><h1>"+maKichHoat+"</h1></body></html>";
        text+="<br/> Click vào đường link để kích hoạt tài khoản: ";
        String url = "http://localhost:3000/kich-hoat/"+email+"/"+maKichHoat;
        text+=("<br/> <a href="+url+">"+url+"</a> ");

        emailService.sendMessage("vdm24032002.email@gmail.com", email, subject, text);
    }

    private void guiEmailQuenMatKhau(String email, String matKhau) {
        String subject = "Reset mật khẩu";
        String message = "Mật khẩu tạm thời của bạn là: <strong>" + matKhau + "</strong>";
        message += "<br/> <span>Vui lòng đăng nhập và đổi lại mật khẩu của bạn</span>";
        try {
            emailService.sendMessage("dongph.0502@gmail.com", email, subject, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> kichHoatTaiKhoan(String email, String maKichHoat) {
        // tim nguoi dung theo dia chi email
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email);

        if (nguoiDung == null) {
            return ResponseEntity.badRequest().body(new ThongBao("Người dùng không tồn tại!"));
        }

        if (nguoiDung.isDaKichHoat()) {
            return ResponseEntity.badRequest().body(new ThongBao("Tài khoản đã được kích hoạt!"));
        }

        if (maKichHoat.equals(nguoiDung.getMaKichHoat())) {
            nguoiDung.setDaKichHoat(true);
            nguoiDungRepository.save(nguoiDung);
            return ResponseEntity.ok("Kích hoạt tài khoản thành công!");
        } else {
            return ResponseEntity.badRequest().body(new ThongBao("Mã kích hoạt không chính xác!"));
        }
    }


    @Override
    public ResponseEntity<?> capNhap(JsonNode nguoiDungJson, String option) {
        try{
            NguoiDung nguoiDung = objectMapper.treeToValue(nguoiDungJson, NguoiDung.class);

            // Kiểm tra nguoiDungname đã tồn tại chưa
            if (!option.equals("update")) {
                if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
                    return ResponseEntity.badRequest().body(new ThongBao("NguoiDungname đã tồn tại."));
                }

                // Kiểm tra email
                if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
                    return ResponseEntity.badRequest().body(new ThongBao("Email đã tồn tại."));
                }
            }

//            // Set ngày sinh cho nguoiDung
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//            Instant instant = Instant.from(formatter.parse(dinhDangChuoiByJson(String.valueOf(nguoiDungJson.get("dateOfBirth")))) );
//            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());
//            nguoiDung.setDateOfBirth(dateOfBirth);

            // Set Quyen cho nguoiDung
            int idRoleRequest = Integer.parseInt(String.valueOf(nguoiDungJson.get("quyen")));
            Optional<Quyen> quyen = quyenRepository.findById(idRoleRequest);
            List<Quyen> danhSachQuyen = new ArrayList<>();
            danhSachQuyen.add(quyen.get());
            nguoiDung.setDanhSachQuyen(danhSachQuyen);

            // Mã hoá mật khẩu
            if (!(nguoiDung.getMatKhau() == null)) { // Trường hợp là thêm hoặc thay đổi password
                String encodePassword = passwordEncoder.encode(nguoiDung.getMatKhau());
                nguoiDung.setMatKhau(encodePassword);
            } else {
                // Trường hợp cho update không thay đổi password
                Optional<NguoiDung> nguoiDungTemp = nguoiDungRepository.findById(nguoiDung.getMaNguoiDung());
                nguoiDung.setMatKhau(nguoiDungTemp.get().getMatKhau());
            }

            // Set avatar
            String avatar = (dinhDangChuoiByJson(String.valueOf((nguoiDungJson.get("avatar")))));
            if (avatar.length() > 500) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(avatar);
                String avatarURL = capNhapHinhAnhService.capNhapHinhAnh(avatarFile, "NguoiDung_" + nguoiDung.getMaNguoiDung());
                nguoiDung.setAvatar(avatarURL);
            }

            nguoiDungRepository.save(nguoiDung);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    @Override
    public ResponseEntity<?> xoa(int id) {
        try{
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(id);
            // co ton tai
            if (nguoiDung.isPresent()) {
                // lay avatar
                String imageUrl = nguoiDung.get().getAvatar();
                // co avatar thi xoa di
                if (imageUrl != null) {
                    capNhapHinhAnhService.xoaAnh(imageUrl);
                }
                // xoa nguoi dung
                nguoiDungRepository.deleteById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    @Override
    public ResponseEntity<?> thayDoiMatKhau(JsonNode nguoiDungJson) {
        try{
            int idNguoiDung = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(nguoiDungJson.get("maNguoiDung"))));
            String newPassword = dinhDangChuoiByJson(String.valueOf(nguoiDungJson.get("matKhauMoi")));
            System.out.println(idNguoiDung);
            System.out.println(newPassword);
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(idNguoiDung);
            nguoiDung.get().setMatKhau(passwordEncoder.encode(newPassword));
            nguoiDungRepository.save(nguoiDung.get());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> thayDoiAvatar(JsonNode nguoiDungJson) {
        try{
            int idNguoiDung = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(nguoiDungJson.get("maNguoiDung"))));
            String dataAvatar = dinhDangChuoiByJson(String.valueOf(nguoiDungJson.get("avatar")));

            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(idNguoiDung);

            // Xoá đi ảnh trước đó trong cloudinary
            if (nguoiDung.get().getAvatar().length() > 0) {
                capNhapHinhAnhService.xoaAnh(nguoiDung.get().getAvatar());
            }

            if (Base64ToMultipartFileConverter.isBase64(dataAvatar)) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(dataAvatar);
                String avatarUrl = capNhapHinhAnhService.capNhapHinhAnh(avatarFile, "NguoiDung_" + idNguoiDung);
                nguoiDung.get().setAvatar(avatarUrl);
            }

            NguoiDung newNguoiDung =  nguoiDungRepository.save(nguoiDung.get());
            final String jwtToken = jwtService.generateToken(newNguoiDung.getTenDangNhap());
            return ResponseEntity.ok(new JwtResponse(jwtToken));

        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> capNhapProfile(JsonNode nguoiDungJson) {
        try{
            NguoiDung nguoiDungRequest = objectMapper.treeToValue(nguoiDungJson, NguoiDung.class);
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(nguoiDungRequest.getMaNguoiDung());

            nguoiDung.get().setHoDem(nguoiDungRequest.getHoDem());
            nguoiDung.get().setTen(nguoiDungRequest.getTen());
//            // Format lại ngày sinh
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//            Instant instant = Instant.from(formatter.parse(formatStringByJson(String.valueOf(nguoiDungJson.get("dateOfBirth")))));
//            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());

//            nguoiDung.get().setDateOfBirth(dateOfBirth);
            nguoiDung.get().setSoDienThoai(nguoiDungRequest.getSoDienThoai());
            nguoiDung.get().setDiaChiGiaoHang(nguoiDungRequest.getDiaChiGiaoHang());
            nguoiDung.get().setGioiTinh(nguoiDungRequest.getGioiTinh());

            nguoiDungRepository.save(nguoiDung.get());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> quyenMatKhau(JsonNode jsonNode) {
        try{
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(dinhDangChuoiByJson(jsonNode.get("email").toString()));

            if (nguoiDung == null) {
                return ResponseEntity.notFound().build();
            }

            // Đổi mật khẩu cho nguoiDung
            String passwordTemp = taoMatKhauTamThoi();
            nguoiDung.setMatKhau(passwordEncoder.encode(passwordTemp));
            nguoiDungRepository.save(nguoiDung);

            // Gửi email đê nhận mật khẩu
            guiEmailQuenMatKhau(nguoiDung.getEmail(), passwordTemp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> themNguoiDung(JsonNode nguoiDungJson, String option) {
        try{
            NguoiDung nguoiDung = objectMapper.treeToValue(nguoiDungJson, NguoiDung.class);

            // Kiểm tra ten dang nhap đã tồn tại chưa
            if (!option.equals("cap-nhap")) {
                if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
                    return ResponseEntity.badRequest().body(new ThongBao("Tên đăng nhập đã tồn tại."));
                }

                // Kiểm tra email
                if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
                    return ResponseEntity.badRequest().body(new ThongBao("Email đã tồn tại."));
                }
            }


            // Set role cho nguoiDung
            int idRoleRequest = Integer.parseInt(String.valueOf(nguoiDungJson.get("role")));
            Optional<Quyen> quyen = quyenRepository.findById(idRoleRequest);
            List<Quyen> danhSachQuyen = new ArrayList<>();
            danhSachQuyen.add(quyen.get());
            nguoiDung.setDanhSachQuyen(danhSachQuyen);

            // Mã hoá mật khẩu
            if (!(nguoiDung.getMatKhau() == null)) { // Trường hợp là thêm hoặc thay đổi password
                String encodePassword = passwordEncoder.encode(nguoiDung.getMatKhau());
                nguoiDung.setMatKhau(encodePassword);
            } else {
                // Trường hợp cho update không thay đổi password
                Optional<NguoiDung> nguoiDungTemp = nguoiDungRepository.findById(nguoiDung.getMaNguoiDung());
                nguoiDung.setMatKhau(nguoiDungTemp.get().getMatKhau());
            }

            // Set avatar
            String avatar = (dinhDangChuoiByJson(String.valueOf((nguoiDungJson.get("avatar")))));
            if (avatar.length() > 500) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(avatar);
                String avatarURL = capNhapHinhAnhService.capNhapHinhAnh(avatarFile, "NguoiDung_" + nguoiDung.getMaNguoiDung());
                nguoiDung.setAvatar(avatarURL);
            }

            nguoiDungRepository.save(nguoiDung);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    private String dinhDangChuoiByJson(String json) {
        return json.replaceAll("\"", "");
    }

    private String taoMatKhauTamThoi() {
        return RandomStringUtils.random(10, true, true);
    }
}

