package com.example.webbansach_backend.service.nguoidung;

import com.example.webbansach_backend.dao.NguoiDungRepository;
import com.example.webbansach_backend.dao.QuyenRepository;
import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.entity.Quyen;
import com.example.webbansach_backend.entity.ThongBao;
import com.example.webbansach_backend.service.capnhaphinhanh.CapNhapHinhAnhService;
import com.example.webbansach_backend.service.email.EmailService;
import com.example.webbansach_backend.service.util.Base64ToMultipartFileConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            // Kiểm tra username đã tồn tại chưa
            if (!option.equals("update")) {
                if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
                    return ResponseEntity.badRequest().body(new ThongBao("Username đã tồn tại."));
                }

                // Kiểm tra email
                if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
                    return ResponseEntity.badRequest().body(new ThongBao("Email đã tồn tại."));
                }
            }

//            // Set ngày sinh cho user
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//            Instant instant = Instant.from(formatter.parse(dinhDangChuoiByJson(String.valueOf(nguoiDungJson.get("dateOfBirth")))) );
//            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());
//            nguoiDung.setDateOfBirth(dateOfBirth);

            // Set Quyen cho user
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
        return null;
    }

    @Override
    public ResponseEntity<?> thayDoiMatKhau(JsonNode nguoiDungJson) {
        return null;
    }

    @Override
    public ResponseEntity<?> thayDoiAvatar(JsonNode nguoiDungJson) {
        return null;
    }

    @Override
    public ResponseEntity<?> capNhapProfile(JsonNode nguoiDungJson) {
        return null;
    }

    @Override
    public ResponseEntity<?> quyenMatKhau(JsonNode nguoiDungJson) {
        return null;
    }


    private String dinhDangChuoiByJson(String json) {
        return json.replaceAll("\"", "");
    }
}

