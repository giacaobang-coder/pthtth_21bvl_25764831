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
 * Hướng dẫn thực hành B1-B5: tạo bảng NhanVien, thêm 3 nhân viên trong một giao dịch,
 * truy vấn in ra màn hình, đóng tài nguyên theo thứ tự ngược lại trong finally.
 */
public class NhanVienDemo {
    private static final String DB_DIR = "data/Lab08_JDBC_20261117";
    private static final String URL = "jdbc:sqlite:" + DB_DIR + "/nhanvien.db";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Files.createDirectories(Path.of(DB_DIR));

            // B1. Nạp driver và thiết lập kết nối (SQLite không cần username, password)
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL, "", "");
            System.out.println("B1. Đã kết nối " + URL);

            // B2. Tạo Statement từ Connection
            stmt = conn.createStatement();
            System.out.println("B2. Đã tạo Statement");

            // B3.1 Tạo bảng (xóa bảng cũ để chạy lại nhiều lần vẫn ra cùng kết quả)
            stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien");
            String sqlCreateTable = "CREATE TABLE NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT)";
            stmt.executeUpdate(sqlCreateTable);
            System.out.println("B3.1 Đã tạo bảng NhanVien");

            // B3.2 Thêm dữ liệu trong một giao dịch: chỉ commit khi cả 3 lệnh đều thành công
            String[] sqlInserts = {
                    "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (1, 'Nguyễn Văn An', 'Trưởng phòng')",
                    "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (2, 'Trần Thị Bình', 'Kế toán')",
                    "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (3, 'Lê Văn Cường', 'Lập trình viên')"
            };
            conn.setAutoCommit(false);
            for (String sqlInsert : sqlInserts) {
                stmt.executeUpdate(sqlInsert);
            }
            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("B3.2 Đã thêm " + sqlInserts.length + " nhân viên (commit)");

            // B4. Truy vấn và hiển thị dữ liệu
            String sqlSelect = "SELECT id, ten, chuc_vu FROM NhanVien";
            rs = stmt.executeQuery(sqlSelect);
            System.out.println("B4. ===== DANH SÁCH NHÂN VIÊN =====");
            while (rs.next()) {
                System.out.println("%d. %s - %s".formatted(
                        rs.getInt("id"), rs.getString("ten"), rs.getString("chuc_vu")));
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver SQLite (thiếu lib/sqlite-jdbc-*.jar): " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            rollback(conn);
        } catch (IOException e) {
            System.err.println("Không tạo được thư mục " + DB_DIR + ": " + e.getMessage());
        } finally {
            // B5. Đóng tài nguyên theo thứ tự ngược lại, kể cả khi có lỗi
            close(rs, "ResultSet");
            close(stmt, "Statement");
            close(conn, "Connection");
        }
    }

    private static void rollback(Connection conn) {
        if (conn == null) return;
        try {
            if (!conn.getAutoCommit()) {
                conn.rollback();
                System.err.println("Đã rollback, hủy toàn bộ thay đổi trong giao dịch");
            }
        } catch (SQLException e) {
            System.err.println("Rollback thất bại: " + e.getMessage());
        }
    }

    private static void close(AutoCloseable resource, String name) {
        if (resource == null) return;
        try {
            resource.close();
            System.out.println("B5. Đã đóng " + name);
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng " + name + ": " + e.getMessage());
        }
    }
}
