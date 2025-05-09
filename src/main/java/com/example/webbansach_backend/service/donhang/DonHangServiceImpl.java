package com.example.webbansach_backend.service.donhang;

import com.example.webbansach_backend.dao.*;
import com.example.webbansach_backend.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DonHangServiceImpl implements DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;
    private final ObjectMapper objectMapper;

    public DonHangServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<?> themDonHang(JsonNode jsonData) {
        try{

            DonHang donHangData = objectMapper.treeToValue(jsonData, DonHang.class);
            donHangData.setTongTien(donHangData.getTongTienSanPham());
            donHangData.setNgayTao(Date.valueOf(LocalDate.now()));
            donHangData.setTrangThai("Đang xử lý");

            int maNguoiDung = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonData.get("maNguoiDung"))));
            Optional<NguoiDung> user = nguoiDungRepository.findById(maNguoiDung);
            donHangData.setNguoiDung(user.get());

            int maHinhThucThanhToan = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonData.get("maHinhThucThanhToan"))));
            Optional<HinhThucThanhToan> payment = hinhThucThanhToanRepository.findById(maHinhThucThanhToan);
            donHangData.setHinhThucThanhToan(payment.get());

            DonHang newOrder = donHangRepository.save(donHangData);

            JsonNode jsonNode = jsonData.get("sach");
            for (JsonNode node : jsonNode) {
                int quantity = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(node.get("soLuong"))));
                Sach bookResponse = objectMapper.treeToValue(node.get("sach"), Sach.class);
                Optional<Sach> sach = sachRepository.findById(bookResponse.getMaSach());
                sach.get().setSoLuong(sach.get().getSoLuong() - quantity);
                sach.get().setSoLuongBan(sach.get().getSoLuongBan() + quantity);

                ChiTietDonHang chiTietDonHang = new ChiTietDonHang();
                chiTietDonHang.setSach(sach.get());
                chiTietDonHang.setSoLuong(quantity);
                chiTietDonHang.setDonHang(newOrder);
                chiTietDonHang.setGiaBan(quantity * sach.get().getGiaBan());
                chiTietDonHang.setReview(false);
                chiTietDonHangRepository.save(chiTietDonHang);
                sachRepository.save(sach.get());
            }

            gioHangRepository.deleteGioHangsByMaNguoiDung(user.get().getMaNguoiDung());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> capNhapDonHang(JsonNode jsonData) {
        try{
            int maDonHang =  Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonData.get("maDonHang"))));
            String trangThai = dinhDangChuoiByJson(String.valueOf(jsonData.get("trangThai")));
            Optional<DonHang> donHang = donHangRepository.findById(maDonHang);
            donHang.get().setTrangThai(trangThai);

            // Lấy ra order detail
            if (trangThai.equals("Bị huỷ")) {
                List<ChiTietDonHang> danhSachChiTietDonHang = chiTietDonHangRepository.findChiTietDonHangByDonHang(donHang.get());
                for (ChiTietDonHang chiTietDonHang : danhSachChiTietDonHang) {
                    Sach bookOrderDetail = chiTietDonHang.getSach();
                    bookOrderDetail.setSoLuongBan(bookOrderDetail.getSoLuongBan() - chiTietDonHang.getSoLuong());
                    bookOrderDetail.setSoLuong(bookOrderDetail.getSoLuong() + chiTietDonHang.getSoLuong());
                    sachRepository.save(bookOrderDetail);
                }
            }

            donHangRepository.save(donHang.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> huyDonHang(JsonNode jsonData) {
        try{
            int maNguoiDung = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonData.get("maNguoiDung"))));
            NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).get();

            DonHang donHang = donHangRepository.findFirstByNguoiDungOrderByMaDonHangDesc(nguoiDung);
            donHang.setTrangThai("Bị huỷ");

            List<ChiTietDonHang> danhSachChiTietDonHang = chiTietDonHangRepository.findChiTietDonHangByDonHang(donHang);
            for (ChiTietDonHang chiTietDonHang : danhSachChiTietDonHang) {
                Sach bookOrderDetail = chiTietDonHang.getSach();
                bookOrderDetail.setSoLuongBan(bookOrderDetail.getSoLuongBan() - chiTietDonHang.getSoLuong());
                bookOrderDetail.setSoLuong(bookOrderDetail.getSoLuong() + chiTietDonHang.getSoLuong());
                sachRepository.save(bookOrderDetail);
            }

            donHangRepository.save(donHang);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    private String dinhDangChuoiByJson(String json) {
        return json.replaceAll("\"", "");
    }

}
