package com.example.webbansach_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "sach")
public class Sach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_sach")
    private int maSach;

    @Column(name = "ten_sach",length = 256)
    private String tenSach;

    @Column(name = "ten_tac_gia", length = 512)
    private String tenTacGia;

    // ISBN ma so xuat ban the gioi
    @Column(name = "isbn",length = 256)
    private String ISBN;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "gia_niem_yet")
    private double giaNiemYet;

    @Column(name = "gia_ban")
    private double giaBan;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "trung_binh_xep_hang")
    private Double trungBinhXepHang;

    // khi xoa 1 quyen sach thi ko the xoa di the loai nen ko the de all

    @ManyToMany(fetch = FetchType.LAZY, // lazy chỉ được tải khi bạn gọi đến nó (lazy loading). Tối ưu hiệu năng khi không cần luôn luôn tải kèm dữ liệu liên quan.
            cascade = { // Chỉ định những hành động nào sẽ tự động áp dụng từ Sach sang TheLoai
                    CascadeType.PERSIST, //Khi lưu Sach, tự động lưu TheLoai nếu chưa có
                    CascadeType.MERGE, //Khi cập nhật Sach, tự động cập nhật TheLoai
                    CascadeType.REFRESH, //Khi làm mới Sach, cũng làm mới TheLoai
                    CascadeType.DETACH //Khi tách Sach khỏi EntityManager, TheLoai cũng bị tách theo
            })
    @JoinTable( // tên bảng trung gian
            name = "sach_theloai",
            joinColumns = @JoinColumn(name = "ma_sach"), // cot dai dien cho entity hien tai
            inverseJoinColumns = @JoinColumn(name = "ma_the_loai") // cot dai dien cho entity con lai
    )
    List<TheLoai> danhSachTheLoai;

    // khi xoa di 1 quyen sach thi co the xoa luon hinh anh cua no cx nhu binh luan nen de all

    @OneToMany(mappedBy = "sach", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<HinhAnh> danhSachHinhAnh;

    @OneToMany(mappedBy = "sach", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<SuDanhGia> danhSachSuDanhGia;

    @OneToMany(mappedBy = "sach", fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    List<ChiTietDonHang> danhSachChiTietDonHang;

    @OneToMany(mappedBy = "sach", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<SachYeuThich> danhSachSachYeuThich;
}
