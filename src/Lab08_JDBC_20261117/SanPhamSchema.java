package Lab08_JDBC_20261117;

/**
 * Cấu hình dùng chung cho các bài về bảng SanPham (bài 1-4): đường dẫn CSDL,
 * câu lệnh tạo bảng và 3 sản phẩm mẫu.
 */
public final class SanPhamSchema {
    public static final String DB_DIR = "data/Lab08_JDBC_20261117";
    public static final String URL = "jdbc:sqlite:" + DB_DIR + "/sanpham.db";

    // CHECK chặn giá và số lượng âm ngay ở CSDL (bài 2 dựa vào ràng buộc này để rollback)
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS SanPham ("
            + "maSP TEXT PRIMARY KEY, "
            + "tenSP TEXT NOT NULL, "
            + "gia REAL NOT NULL CHECK (gia >= 0), "
            + "soluong INTEGER NOT NULL CHECK (soluong >= 0))";

    public static final String[][] SAMPLE = {
            {"SP01", "Laptop Dell Inspiron", "15000000", "10"},
            {"SP02", "Chuột Logitech", "350000", "50"},
            {"SP03", "Bàn phím cơ", "1200000", "20"}
    };

    private SanPhamSchema() {
    }
}
