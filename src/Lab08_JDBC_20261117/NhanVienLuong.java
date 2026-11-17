package Lab08_JDBC_20261117;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài 5 - Truy vấn nâng cao với hàm tổng hợp trên bảng NhanVien có thêm cột luong:
 * SUM, AVG, MIN/MAX và COUNT kết hợp GROUP BY.
 */
public class NhanVienLuong {
    private static final String DB_DIR = "data/Lab08_JDBC_20261117";
    private static final String URL = "jdbc:sqlite:" + DB_DIR + "/nhanvien.db";

    private static final Object[][] NHAN_VIEN = {
            {1, "Nguyễn Văn An", "Trưởng phòng", 35_000_000},
            {2, "Trần Thị Bình", "Kế toán", 15_000_000},
            {3, "Lê Văn Cường", "Lập trình viên", 22_000_000},
            {4, "Phạm Thị Dung", "Lập trình viên", 25_000_000},
            {5, "Hoàng Văn Em", "Lập trình viên", 18_000_000},
            {6, "Võ Thị Giang", "Kế toán", 16_500_000},
            {7, "Đặng Văn Hùng", "Kiểm thử", 14_000_000},
            {8, "Bùi Thị Lan", "Kiểm thử", 13_500_000}
    };

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of(DB_DIR));
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            System.err.println("Không khởi động được: " + e);
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            createAndInsert(conn, stmt);

            System.out.println("===== DANH SÁCH NHÂN VIÊN =====");
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM NhanVien ORDER BY id")) {
                while (rs.next()) {
                    System.out.println("%d. %-15s %-15s %,12.0f VND".formatted(rs.getInt("id"),
                            rs.getString("ten"), rs.getString("chuc_vu"), rs.getDouble("luong")));
                }
            }

            System.out.println();
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT SUM(luong) AS tong, AVG(luong) AS tb, COUNT(*) AS so FROM NhanVien")) {
                rs.next();
                System.out.println("Tổng lương  SUM(luong): %,.0f VND".formatted(rs.getDouble("tong")));
                System.out.println("Lương TB    AVG(luong): %,.0f VND".formatted(rs.getDouble("tb")));
                System.out.println("Số nhân viên  COUNT(*): " + rs.getInt("so"));
            }

            // Dùng truy vấn con để lấy cả tên người có lương cao nhất/thấp nhất (có thể nhiều người bằng nhau)
            printPerson(stmt, "Lương cao nhất MAX(luong)",
                    "SELECT ten, luong FROM NhanVien WHERE luong = (SELECT MAX(luong) FROM NhanVien)");
            printPerson(stmt, "Lương thấp nhất MIN(luong)",
                    "SELECT ten, luong FROM NhanVien WHERE luong = (SELECT MIN(luong) FROM NhanVien)");

            System.out.println();
            System.out.println("===== SỐ NHÂN VIÊN THEO CHỨC VỤ (COUNT + GROUP BY) =====");
            try (ResultSet rs = stmt.executeQuery("SELECT chuc_vu, COUNT(*) AS so_luong, AVG(luong) AS tb "
                    + "FROM NhanVien GROUP BY chuc_vu ORDER BY so_luong DESC, chuc_vu")) {
                while (rs.next()) {
                    System.out.println("%-15s %d người, lương TB %,.0f VND".formatted(
                            rs.getString("chuc_vu"), rs.getInt("so_luong"), rs.getDouble("tb")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
        }
    }

    private static void createAndInsert(Connection conn, Statement stmt) throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien");
        stmt.executeUpdate("CREATE TABLE NhanVien ("
                + "id INTEGER PRIMARY KEY, "
                + "ten TEXT NOT NULL, "
                + "chuc_vu TEXT, "
                + "luong REAL NOT NULL CHECK (luong >= 0))");

        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (?, ?, ?, ?)")) {
            for (Object[] nv : NHAN_VIEN) {
                ps.setInt(1, (Integer) nv[0]);
                ps.setString(2, (String) nv[1]);
                ps.setString(3, (String) nv[2]);
                ps.setDouble(4, (Integer) nv[3]);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static void printPerson(Statement stmt, String title, String sql) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("%-27s %s - %,.0f VND".formatted(
                        title + ":", rs.getString("ten"), rs.getDouble("luong")));
            }
        }
    }
}
