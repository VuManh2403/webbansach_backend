package com.example.webbansach_backend.service.giohang;

import com.example.webbansach_backend.dao.GioHangRepository;
import com.example.webbansach_backend.dao.NguoiDungRepository;
import com.example.webbansach_backend.entity.GioHang;
import com.example.webbansach_backend.entity.NguoiDung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GioHangServiceImpl implements GioHangService {
    @Autowired
    public NguoiDungRepository nguoiDungRepository;

    @Autowired
    public GioHangRepository gioHangrepository;

    private final ObjectMapper objectMapper;

    public GioHangServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public ResponseEntity<?> themGioHang(JsonNode jsonNode) {
        try{
            int maNguoiDung = 0;
            // Danh sách item của data vừa truyền
            List<GioHang> danhSachGioHangData = new ArrayList<>();
            for (JsonNode jsonDatum : jsonNode) {
                GioHang gioHangData = objectMapper.treeToValue(jsonDatum, GioHang.class);
                maNguoiDung = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonDatum.get("maNguoiDung"))));
                danhSachGioHangData.add(gioHangData);
            }
            Optional<NguoiDung> nguoiDung = nguoiDungRepository.findById(maNguoiDung);
            // Danh sách item của user
            List<GioHang> danhSachGioHang = nguoiDung.get().getDanhSachGioHang();

            // Lặp qua từng item và xử lý
            for (GioHang cartItemData : danhSachGioHangData) {
                boolean isHad = false;
                for (GioHang cartItem : danhSachGioHang) {
                    // Nếu trong cart của user có item đó rồi thì sẽ update lại quantity
                    if (cartItem.getSach().getMaSach() == cartItemData.getSach().getMaSach()) {
                        cartItem.setSoLuong(cartItem.getSoLuong() + cartItemData.getSoLuong());
                        isHad = true;
                        break;
                    }
                }
                // Nếu chưa có thì thêm mới item đó
                if (!isHad) {
                    GioHang gioHang = new GioHang();
                    gioHang.setNguoiDung(nguoiDung.get());
                    gioHang.setSoLuong(cartItemData.getSoLuong());
                    gioHang.setSach(cartItemData.getSach());
                    danhSachGioHang.add(gioHang);
                }
            }
            nguoiDung.get().setDanhSachGioHang(danhSachGioHang);
            NguoiDung newUser = nguoiDungRepository.save(nguoiDung.get());


            if (danhSachGioHangData.size() == 1) {
                List<GioHang> cartItemListTemp = newUser.getDanhSachGioHang();
                return ResponseEntity.ok(cartItemListTemp.get(danhSachGioHang.size() - 1).getMaGioHang());
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<?> capNhapGioHang(JsonNode jsonNode) {
        try{
            int maGioHang = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonNode.get("maGioHang"))));
            int soLuong = Integer.parseInt(dinhDangChuoiByJson(String.valueOf(jsonNode.get("soLuong"))));
            Optional<GioHang> gioHang = gioHangrepository.findById(maGioHang);
            gioHang.get().setSoLuong(soLuong);
            gioHangrepository.save(gioHang.get());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private String dinhDangChuoiByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
