package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dao.*;
import com.example.webbansach_backend.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequestMapping("/danh-gia")
@RestController
public class DanhGiaController {
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private SuDanhGiaRepository suDanhGiaRepository;
    private final ObjectMapper objectMapper;

    public DanhGiaController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("/them-danh-gia")
    public ResponseEntity<?> saveDanhGia(@RequestBody JsonNode jsonNode) {
        try{
            int maNguoiDung = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonNode.get("maNguoiDung"))));
            int maDonHang = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonNode.get("maDonHang"))));
            int maSach = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonNode.get("maSach"))));
            float diemXepHang = Float.parseFloat(dinhDangChuoiByJson(String.valueOf(jsonNode.get("diemXepHang"))));
            String nhanXet = dinhDangChuoiByJson(String.valueOf(jsonNode.get("nhanXet")));

            NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).get();
            DonHang donHang = donHangRepository.findById(maDonHang).get();
            List<ChiTietDonHang> danhSachChiTietDonHang = chiTietDonHangRepository.findChiTietDonHangByDonHang(donHang);
            Sach sach = sachRepository.findById(maSach).get();

            for (ChiTietDonHang chiTietDonHang : danhSachChiTietDonHang) {
                if (chiTietDonHang.getSach().getMaSach() == maSach) {
                    chiTietDonHang.setReview(true);

                    SuDanhGia danhGia = new SuDanhGia();
                    danhGia.setSach(sach);
                    danhGia.setNguoiDung(nguoiDung);
                    danhGia.setNhanXet(nhanXet);
                    danhGia.setDiemXepHang(diemXepHang);
                    danhGia.setChiTietDonHang(chiTietDonHang);

                    // Lấy thời gian hiện tại
                    Instant instant = Instant.now();
                    // Chuyển đổi thành timestamp
                    Timestamp timestamp = Timestamp.from(instant);
                    danhGia.setThoiGianDanhGia(timestamp);
                    chiTietDonHangRepository.save(chiTietDonHang);
                    suDanhGiaRepository.save(danhGia);
                    break;
                }
            }

            // Set lại rating trung bình của quyển sách đó
            List<SuDanhGia> danhSachDanhGia = suDanhGiaRepository.findAll();
            double sum = 0; // Tổng rating
            int n = 0; // Số lượng rating
            for (SuDanhGia danhGia : danhSachDanhGia) {
                if (danhGia.getSach().getMaSach() == maSach) {
                    n++;
                    sum += danhGia.getDiemXepHang();
                }
            }
            double ratingAvg = sum / n;
            sach.setTrungBinhXepHang(ratingAvg);
            sachRepository.save(sach);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cap-nhap-danh-gia")
    public ResponseEntity<?> updateDanhGia(@RequestBody JsonNode jsonNode) {
        try{
            SuDanhGia danhGiaRequest = objectMapper.treeToValue(jsonNode, SuDanhGia.class);
            SuDanhGia danhGia = suDanhGiaRepository.findById(danhGiaRequest.getMaDanhGia()).get();
            danhGia.setNhanXet(danhGiaRequest.getNhanXet());
            danhGia.setDiemXepHang(danhGiaRequest.getDiemXepHang());

            suDanhGiaRepository.save(danhGia);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/lay-danh-gia")
    public ResponseEntity<?> getDanhGia(@RequestBody JsonNode jsonNode) {
        try{
            int maDonHang = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonNode.get("maDonHang"))));
            int maSach = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonNode.get("maSach"))));

            DonHang donHang = donHangRepository.findById(maDonHang).get();
            Sach sach = sachRepository.findById(maSach).get();
            List<ChiTietDonHang> danhSachChiTietDonHang = chiTietDonHangRepository.findChiTietDonHangByDonHang(donHang);
            for (ChiTietDonHang chiTietDonHang : danhSachChiTietDonHang) {
                if (chiTietDonHang.getSach().getMaSach() == sach.getMaSach()) {
                    SuDanhGia danhGia = suDanhGiaRepository.findDanhGiaByChiTietDonHang(chiTietDonHang);
                    SuDanhGia danhGiaResponse = new SuDanhGia(); // Trả review luôn bị lỗi không được, nên phải dùng cách này
                    danhGiaResponse.setMaDanhGia(danhGia.getMaDanhGia());
                    danhGiaResponse.setNhanXet(danhGia.getNhanXet());
                    danhGiaResponse.setThoiGianDanhGia(danhGia.getThoiGianDanhGia());
                    danhGiaResponse.setDiemXepHang(danhGia.getDiemXepHang());
                    return ResponseEntity.status(HttpStatus.OK).body(danhGiaResponse);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    private String dinhDangChuoiByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
