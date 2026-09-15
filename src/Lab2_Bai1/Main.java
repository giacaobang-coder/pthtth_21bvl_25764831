package Lab2_Bai1;


public class Main {

    public static void main(String[] args) {

        // Tạo sản phẩm 1
        SanPham sp1 = new SanPham(
                "SP01",
                "Laptop",
                15000000,
                10
        );

        // Tạo sản phẩm 2
        SanPham sp2 = new SanPham(
                "SP02",
                "Chuot",
                500000,
                20
        );

        // Hiển thị ban đầu
        System.out.println("===== THONG TIN BAN DAU =====");

        System.out.println("\n--- San pham 1 ---");
        sp1.hienThiThongTin();

        System.out.println("\n--- San pham 2 ---");
        sp2.hienThiThongTin();


        // Nhập thêm hàng
        System.out.println("\n===== NHAP THEM HANG =====");

        sp1.nhapHang(5);

        System.out.println("\n--- Sau khi nhap hang ---");
        sp1.hienThiThongTin();


        // Bán hàng thành công
        System.out.println("\n===== BAN HANG =====");

        boolean ketQua1 = sp1.banHang(3);

        System.out.println("Ket qua ban hang: " + ketQua1);

        System.out.println("\n--- Sau khi ban 3 san pham ---");
        sp1.hienThiThongTin();


        // Thử bán quá số lượng tồn kho
        System.out.println("\n===== THU BAN QUA TON KHO =====");

        boolean ketQua2 = sp1.banHang(100);

        System.out.println("Ket qua ban hang: " + ketQua2);

        System.out.println("\n--- Sau khi thu ban 100 san pham ---");
        sp1.hienThiThongTin();
    }
}