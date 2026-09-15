package Lab2_Bai1;

public class SanPham {

    // 1. Các thuộc tính
    private String maSP;
    private String tenSP;
    private double donGia;
    private int soLuong;

    // 2. Constructor đầy đủ tham số
    public SanPham(String maSP, String tenSP, double donGia, int soLuong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }

    // 3. Getter
    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public double getDonGia() {
        return donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    // 4. Tính thành tiền
    public double tinhThanhTien() {
        return donGia * soLuong;
    }

    // 5. Nhập hàng
    public void nhapHang(int soLuongNhap) {
        if (soLuongNhap > 0) {
            soLuong = soLuong + soLuongNhap;
            System.out.println("Nhap hang thanh cong!");
        } else {
            System.out.println("So luong nhap phai lon hon 0!");
        }
    }

    // 6. Bán hàng
    public boolean banHang(int soLuongBan) {

        if (soLuongBan <= 0) {
            System.out.println("So luong ban phai lon hon 0!");
            return false;
        }

        if (soLuongBan > soLuong) {
            System.out.println("Khong du hang de ban!");
            return false;
        }

        soLuong = soLuong - soLuongBan;
        System.out.println("Ban hang thanh cong!");

        return true;
    }

    // 7. Hiển thị thông tin
    public void hienThiThongTin() {
        System.out.println("Ma san pham: " + maSP);
        System.out.println("Ten san pham: " + tenSP);
        System.out.println("Don gia: " + donGia);
        System.out.println("So luong ton kho: " + soLuong);
        System.out.println("Thanh tien: " + tinhThanhTien());
    }
}