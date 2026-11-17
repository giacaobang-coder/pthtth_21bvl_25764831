package Lab08_JDBC_20261117;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài 2 - Giao dịch (Transaction) khi cập nhật dữ liệu.
 * Giao dịch 1 nhập kho 2 sản phẩm: mọi lệnh thành công nên commit.
 * Giao dịch 2 xuất kho: lệnh thứ hai làm số lượng âm, vi phạm CHECK nên rollback cả lệnh thứ nhất.
 */
public class SanPhamTransaction {
    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of(SanPhamSchema.DB_DIR));
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            System.err.println("Không khởi động được: " + e);
            return;
        }

        try (Connection conn = DriverManager.getConnection(SanPhamSchema.URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(SanPhamSchema.CREATE_TABLE);
            runTransaction(conn, "Khởi tạo lại dữ liệu mẫu", resetSql());
            print(stmt, "Ban đầu");

            runTransaction(conn, "Giao dịch 1: nhập kho SP01 +5, SP02 +3",
                    "UPDATE SanPham SET soluong = soluong + 5 WHERE maSP = 'SP01'",
                    "UPDATE SanPham SET soluong = soluong + 3 WHERE maSP = 'SP02'");
            print(stmt, "Sau giao dịch 1");

            runTransaction(conn, "Giao dịch 2: xuất kho SP01 -2, SP03 -1000 (SP03 chỉ còn 20)",
                    "UPDATE SanPham SET soluong = soluong - 2 WHERE maSP = 'SP01'",
                    "UPDATE SanPham SET soluong = soluong - 1000 WHERE maSP = 'SP03'");
            print(stmt, "Sau giao dịch 2 (SP01 vẫn là 15 vì đã rollback)");
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
        }
    }

    private static void runTransaction(Connection conn, String title, String... sqls) throws SQLException {
        System.out.println();
        System.out.println(">>> " + title);
        conn.setAutoCommit(false);
        try (Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                System.out.println("    " + sql);
                stmt.executeUpdate(sql);
            }
            conn.commit();
            System.out.println("    => COMMIT: tất cả thay đổi được lưu");
        } catch (SQLException e) {
            System.err.println("    Lỗi: " + e.getMessage());
            conn.rollback();
            System.out.println("    => ROLLBACK: hủy toàn bộ thay đổi của giao dịch");
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static String[] resetSql() {
        String[] sqls = new String[SanPhamSchema.SAMPLE.length + 1];
        sqls[0] = "DELETE FROM SanPham";
        for (int i = 0; i < SanPhamSchema.SAMPLE.length; i++) {
            String[] sp = SanPhamSchema.SAMPLE[i];
            sqls[i + 1] = "INSERT INTO SanPham VALUES ('" + sp[0] + "', '" + sp[1] + "', " + sp[2] + ", " + sp[3] + ")";
        }
        return sqls;
    }

    private static void print(Statement stmt, String title) throws SQLException {
        System.out.println("----- " + title + " -----");
        try (ResultSet rs = stmt.executeQuery("SELECT maSP, tenSP, soluong FROM SanPham ORDER BY maSP")) {
            while (rs.next()) {
                System.out.println("%-6s %-25s SL = %d".formatted(
                        rs.getString("maSP"), rs.getString("tenSP"), rs.getInt("soluong")));
            }
        }
    }
}
