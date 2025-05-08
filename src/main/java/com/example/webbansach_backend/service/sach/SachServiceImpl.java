package com.example.webbansach_backend.service.sach;

import com.example.webbansach_backend.dao.HinhAnhRepository;
import com.example.webbansach_backend.dao.SachRepository;
import com.example.webbansach_backend.dao.TheLoaiRepository;
import com.example.webbansach_backend.entity.HinhAnh;
import com.example.webbansach_backend.entity.Sach;
import com.example.webbansach_backend.entity.TheLoai;
import com.example.webbansach_backend.service.capnhaphinhanh.CapNhapHinhAnhService;
import com.example.webbansach_backend.service.util.Base64ToMultipartFileConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SachServiceImpl implements SachService {
    private final ObjectMapper objectMapper;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private TheLoaiRepository theLoaiRepository;
    @Autowired
    private HinhAnhRepository hinhAnhRepository;
    @Autowired
    private CapNhapHinhAnhService capNhapHinhAnhService;

    public SachServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode sachJson) {
        try {
            Sach sach = objectMapper.treeToValue(sachJson, Sach.class);

            // Lưu thể loại của sách
            List<Integer> maTheLoaiList = objectMapper.readValue(sachJson.get("maTheLoai").traverse(), new TypeReference<List<Integer>>() {
            });
            List<TheLoai> danhSachTheLoai = new ArrayList<>();
            for (int maTheLoai : maTheLoaiList) {
                Optional<TheLoai> theLoai = theLoaiRepository.findById(maTheLoai);
                danhSachTheLoai.add(theLoai.get());
            }
            sach.setDanhSachTheLoai(danhSachTheLoai);

            // Lưu trước để lấy id sách đặt tên cho ảnh
            Sach sachMoi = sachRepository.save(sach);

            // Lưu thumbnail cho ảnh
            String dataThumbnail = dinhDangChuoiByJson(String.valueOf((sachJson.get("thumbnail"))));

            HinhAnh thumbnail = new HinhAnh();
            thumbnail.setSach(sachMoi);
//            thumbnail.setDataHinhAnh(dataThumbnail);
            thumbnail.setThumbnail(true);
            MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
            String thumbnailUrl = capNhapHinhAnhService.capNhapHinhAnh(multipartFile, "Sach_" + sachMoi.getMaSach());
            thumbnail.setDuongDan(thumbnailUrl);

            List<HinhAnh> imagesList = new ArrayList<>();
            imagesList.add(thumbnail);


            // Lưu những ảnh có liên quan
            String dataRelatedImg = dinhDangChuoiByJson(String.valueOf((sachJson.get("hinhAnhLienQuan"))));
            List<String> arrDataRelatedImg = objectMapper.readValue(sachJson.get("hinhAnhLienQuan").traverse(), new TypeReference<List<String>>() {
            });

            for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                String img = arrDataRelatedImg.get(i);
                HinhAnh image = new HinhAnh();
                image.setSach(sachMoi);
//                image.setDataHinhAnh(img);
                image.setThumbnail(false);
                MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                String imgURL = capNhapHinhAnhService.capNhapHinhAnh(relatedImgFile, "Sach_" + sachMoi.getMaSach() + "." + i);
                image.setDuongDan(imgURL);
                imagesList.add(image);
            }

            sachMoi.setDanhSachHinhAnh(imagesList);
            // Cập nhật lại ảnh
            sachRepository.save(sachMoi);

            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<?> update(JsonNode sachJson) {
        try {
            Sach sach = objectMapper.treeToValue(sachJson, Sach.class);
            List<HinhAnh> imagesList = hinhAnhRepository.findHinhAnhBySach(sach);

            // Lưu thể loại của sách
            List<Integer> idTheLoaiList = objectMapper.readValue(sachJson.get("maTheLoai").traverse(), new TypeReference<List<Integer>>() {
            });
            List<TheLoai> danhsachTheLoai = new ArrayList<>();
            for (int idTheLoai : idTheLoaiList) {
                Optional<TheLoai> genre = theLoaiRepository.findById(idTheLoai);
                danhsachTheLoai.add(genre.get());
            }
            sach.setDanhSachTheLoai(danhsachTheLoai);

            // Kiểm tra xem thumbnail có thay đổi không
            String dataThumbnail = dinhDangChuoiByJson(String.valueOf((sachJson.get("thumbnail"))));
            if (Base64ToMultipartFileConverter.isBase64(dataThumbnail)) {
                for (HinhAnh image : imagesList) {
                    if (image.isThumbnail()) {
//                        image.setDataHinhAnh(dataThumbnail);
                        MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
                        String thumbnailUrl = capNhapHinhAnhService.capNhapHinhAnh(multipartFile, "Sach_" + sach.getMaSach());
                        image.setDuongDan(thumbnailUrl);
                        hinhAnhRepository.save(image);
                        break;
                    }
                }
            }

            Sach newSach = sachRepository.save(sach);

            // Kiểm tra ảnh có liên quan
            List<String> arrDataRelatedImg = objectMapper.readValue(sachJson.get("hinhAnhLienQuan").traverse(), new TypeReference<List<String>>() {});

            // Xem có xoá tất ở bên FE không
            boolean isCheckDelete = true;

            for (String img : arrDataRelatedImg) {
                if (!Base64ToMultipartFileConverter.isBase64(img)) {
                    isCheckDelete = false;
                }
            }

            // Nếu xoá hết tất cả
            if (isCheckDelete) {
                hinhAnhRepository.deleteHinhAnhWithFalseThumbnailByMaSach(newSach.getMaSach());
                HinhAnh thumbnailTemp = imagesList.get(0);
                imagesList.clear();
                imagesList.add(thumbnailTemp);
                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                    String img = arrDataRelatedImg.get(i);
                    HinhAnh image = new HinhAnh();
                    image.setSach(newSach);
//                    image.setDataHinhAnh(img);
                    image.setThumbnail(false);
                    MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                    String imgURL = capNhapHinhAnhService.capNhapHinhAnh(relatedImgFile, "Sach_" + newSach.getMaSach() + "." + i);
                    image.setDuongDan(imgURL);
                    imagesList.add(image);
                }
            } else {
                // Nếu không xoá hết tất cả (Giữ nguyên ảnh hoặc thêm ảnh vào)
                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                    String img = arrDataRelatedImg.get(i);
                    if (Base64ToMultipartFileConverter.isBase64(img)) {
                        HinhAnh image = new HinhAnh();
                        image.setSach(newSach);
//                        image.setDataHinhAnh(img);
                        image.setThumbnail(false);
                        MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                        String imgURL = capNhapHinhAnhService.capNhapHinhAnh(relatedImgFile, "Sach_" + newSach.getMaSach() + "." + i);
                        image.setDuongDan(imgURL);
                        hinhAnhRepository.save(image);
                    }
                }
            }

            newSach.setDanhSachHinhAnh(imagesList);
            // Cập nhật lại ảnh
            sachRepository.save(newSach);

            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public long layTongSoSach() {
        return sachRepository.count();
    }

    private String dinhDangChuoiByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
