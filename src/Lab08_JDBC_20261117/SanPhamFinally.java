package Lab08_JDBC_20261117;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài 3 - Làm lại các thao tác CRUD của bài 1 với try-catch-finally nghiêm ngặt.
 * Connection, Statement, ResultSet được đóng thủ công trong finally theo thứ tự ngược lại.
 * Bước cuối cố ý truy vấn bảng không tồn tại để thấy finally vẫn đóng đủ tài nguyên khi có SQLException.
 */
public class SanPhamFinally {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Files.createDirectories(Path.of(SanPhamSchema.DB_DIR));
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(SanPhamSchema.URL);
            stmt = conn.createStatement();
            stmt.executeUpdate(SanPhamSchema.CREATE_TABLE);
            stmt.executeUpdate("DELETE FROM SanPham");

            System.out.println(">>> Create: thêm 3 sản phẩm");
            for (String[] sp : SanPhamSchema.SAMPLE) {
                stmt.executeUpdate("INSERT INTO SanPham VALUES ('" + sp[0] + "', '" + sp[1] + "', "
                        + sp[2] + ", " + sp[3] + ")");
                System.out.println("    Đã thêm " + sp[0] + " - " + sp[1]);
            }

            System.out.println(">>> Read: liệt kê tất cả");
            rs = stmt.executeQuery("SELECT * FROM SanPham ORDER BY maSP");
            print(rs);
            rs.close();

            System.out.println(">>> Read: tìm sản phẩm có tên chứa \"Chuột\"");
            rs = stmt.executeQuery("SELECT * FROM SanPham WHERE tenSP LIKE '%Chuột%'");
            print(rs);
            rs.close();

            System.out.println(">>> Update: đổi giá SP02 thành 299000, số lượng SP01 thành 8");
            stmt.executeUpdate("UPDATE SanPham SET gia = 299000 WHERE maSP = 'SP02'");
            stmt.executeUpdate("UPDATE SanPham SET soluong = 8 WHERE maSP = 'SP01'");

            System.out.println(">>> Delete: xóa SP03");
            stmt.executeUpdate("DELETE FROM SanPham WHERE maSP = 'SP03'");

            rs = stmt.executeQuery("SELECT * FROM SanPham ORDER BY maSP");
            print(rs);
            rs.close();

            System.out.println(">>> Cố ý truy vấn bảng không tồn tại để tạo SQLException");
            rs = stmt.executeQuery("SELECT * FROM BangKhongTonTai");
            print(rs);
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver SQLite: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Bắt được SQLException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Không tạo được thư mục CSDL: " + e.getMessage());
        } finally {
            // Mỗi tài nguyên đóng trong try-catch riêng để lỗi khi đóng cái này không làm bỏ sót cái khác
            System.out.println(">>> finally: đóng tài nguyên theo thứ tự ngược lại");
            if (rs != null) {
                try {
                    rs.close();
                    System.out.println("    Đã đóng ResultSet");
                } catch (SQLException e) {
                    System.err.println("    Lỗi đóng ResultSet: " + e.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                    System.out.println("    Đã đóng Statement");
                } catch (SQLException e) {
                    System.err.println("    Lỗi đóng Statement: " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("    Đã đóng Connection");
                } catch (SQLException e) {
                    System.err.println("    Lỗi đóng Connection: " + e.getMessage());
                }
            }
        }
    }

    private static void print(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println("    %-6s %-25s %,12.0f VND  SL = %d".formatted(
                    rs.getString("maSP"), rs.getString("tenSP"), rs.getDouble("gia"), rs.getInt("soluong")));
        }
    }
}
