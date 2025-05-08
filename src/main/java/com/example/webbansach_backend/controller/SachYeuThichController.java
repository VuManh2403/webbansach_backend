package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dao.NguoiDungRepository;
import com.example.webbansach_backend.dao.SachRepository;
import com.example.webbansach_backend.dao.SachYeuThichRepository;
import com.example.webbansach_backend.entity.NguoiDung;
import com.example.webbansach_backend.entity.Sach;
import com.example.webbansach_backend.entity.SachYeuThich;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/yeu-thich")
public class SachYeuThichController {
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private SachYeuThichRepository sachYeuThichRepository;

    @GetMapping("/lay-sach-yeu-thich/{maNguoiDung}")
    public ResponseEntity<?> layTatCaSachYeuThichByMaNguoiDung(@PathVariable Integer maNguoiDung) {
        try{
            NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).get();
            List<SachYeuThich> danhSachSachYeuThich = sachYeuThichRepository.findSachYeuThichByNguoiDung(nguoiDung);
            List<Integer> idBookListOfFavoriteBook = new ArrayList<>();
            for (SachYeuThich favoriteBook : danhSachSachYeuThich) {
                idBookListOfFavoriteBook.add(favoriteBook.getSach().getMaSach());
            }
            return ResponseEntity.ok().body(idBookListOfFavoriteBook);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/them-sach-yeu-thich")
    public ResponseEntity<?> themSachYeuThich(@RequestBody JsonNode jsonNode) {
        try{
            int maSach = Integer.parseInt(formatStringByJson(jsonNode.get("maSach").toString()));
            int maNguoiDung = Integer.parseInt(formatStringByJson(jsonNode.get("maNguoiDung").toString()));

            Sach sach = sachRepository.findById(maSach).get();
            NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).get();

            SachYeuThich sachYeuThich = SachYeuThich.builder().sach(sach).nguoiDung(nguoiDung).build();

            sachYeuThichRepository.save(sachYeuThich);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/xoa-sach-yeu-thich")
    public ResponseEntity<?> remove(@RequestBody JsonNode jsonNode) {
        try{
            int maSach = Integer.parseInt(formatStringByJson(jsonNode.get("maSach").toString()));
            int maNguoiDung = Integer.parseInt(formatStringByJson(jsonNode.get("maNguoiDung").toString()));

            Sach sach = sachRepository.findById(maSach).get();
            NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).get();

            SachYeuThich sachYeuThich = sachYeuThichRepository.findSachYeuThichBySachAndNguoiDung(sach, nguoiDung);

            sachYeuThichRepository.delete(sachYeuThich);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}