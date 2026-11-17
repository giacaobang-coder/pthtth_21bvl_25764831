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
 * Bài 4 - PreparedStatement: làm lại CRUD bằng tham số ?, sau đó so sánh với Statement về
 * bảo mật (SQL injection) và hiệu năng (thêm nhiều dòng bằng batch).
 */
public class SanPhamPrepared {
    private static final int ROWS = 5000;

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of(SanPhamSchema.DB_DIR));
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            System.err.println("Không khởi động được: " + e);
            return;
        }

        try (Connection conn = DriverManager.getConnection(SanPhamSchema.URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(SanPhamSchema.CREATE_TABLE);
                stmt.executeUpdate("DELETE FROM SanPham");
            }
            crud(conn);
            compareSecurity(conn);
            comparePerformance(conn);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
        }
    }

    private static void crud(Connection conn) throws SQLException {
        System.out.println(">>> Create: thêm 3 sản phẩm bằng PreparedStatement");
        String insert = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            for (String[] sp : SanPhamSchema.SAMPLE) {
                ps.setString(1, sp[0]);
                ps.setString(2, sp[1]);
                ps.setDouble(3, Double.parseDouble(sp[2]));
                ps.setInt(4, Integer.parseInt(sp[3]));
                ps.executeUpdate();
            }
        }
        // Tên có dấu nháy đơn: với Statement nối chuỗi sẽ lỗi cú pháp, PreparedStatement thì không
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, "SP04");
            ps.setString(2, "Tai nghe Sony 'WH-1000XM5'");
            ps.setDouble(3, 7990000);
            ps.setInt(4, 5);
            ps.executeUpdate();
        }
        list(conn, "SELECT * FROM SanPham ORDER BY maSP");

        System.out.println(">>> Read: tìm tên chứa \"Sony\"");
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM SanPham WHERE tenSP LIKE ?")) {
            ps.setString(1, "%Sony%");
            try (ResultSet rs = ps.executeQuery()) {
                print(rs);
            }
        }

        System.out.println(">>> Update: giá SP02 = 299000, số lượng SP01 = 8");
        try (PreparedStatement ps = conn.prepareStatement("UPDATE SanPham SET gia = ? WHERE maSP = ?")) {
            ps.setDouble(1, 299000);
            ps.setString(2, "SP02");
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("UPDATE SanPham SET soluong = ? WHERE maSP = ?")) {
            ps.setInt(1, 8);
            ps.setString(2, "SP01");
            ps.executeUpdate();
        }

        System.out.println(">>> Delete: xóa SP03");
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM SanPham WHERE maSP = ?")) {
            ps.setString(1, "SP03");
            ps.executeUpdate();
        }
        list(conn, "SELECT * FROM SanPham ORDER BY maSP");
    }

    private static void compareSecurity(Connection conn) throws SQLException {
        String input = "' OR '1'='1";
        System.out.println();
        System.out.println(">>> Bảo mật: người dùng nhập tên cần tìm là  " + input);

        String sql = "SELECT * FROM SanPham WHERE tenSP = '" + input + "'";
        System.out.println("Statement nối chuỗi thành: " + sql);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("=> Statement trả về " + print(rs) + " dòng (lộ toàn bộ bảng!)");
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM SanPham WHERE tenSP = ?")) {
            ps.setString(1, input);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("=> PreparedStatement trả về " + print(rs)
                        + " dòng (chuỗi nhập chỉ được coi là dữ liệu, không phải câu lệnh)");
            }
        }
    }

    private static void comparePerformance(Connection conn) throws SQLException {
        System.out.println();
        System.out.println(">>> Hiệu năng: thêm " + ROWS + " dòng vào bảng tạm (cùng một giao dịch)");
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS SanPhamTam");
            stmt.executeUpdate("CREATE TABLE SanPhamTam (maSP TEXT, tenSP TEXT, gia REAL, soluong INTEGER)");
        }
        conn.setAutoCommit(false);
        try {
            long start = System.nanoTime();
            try (Statement stmt = conn.createStatement()) {
                for (int i = 0; i < ROWS; i++) {
                    stmt.executeUpdate("INSERT INTO SanPhamTam VALUES ('T" + i + "', 'Sản phẩm " + i + "', "
                            + (1000 + i) + ", " + i + ")");
                }
            }
            conn.commit();
            long statementMs = (System.nanoTime() - start) / 1_000_000;

            start = System.nanoTime();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO SanPhamTam VALUES (?, ?, ?, ?)")) {
                for (int i = 0; i < ROWS; i++) {
                    ps.setString(1, "P" + i);
                    ps.setString(2, "Sản phẩm " + i);
                    ps.setDouble(3, 1000 + i);
                    ps.setInt(4, i);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            long preparedMs = (System.nanoTime() - start) / 1_000_000;

            System.out.println("Statement (biên dịch lại câu SQL mỗi lần): " + statementMs + " ms");
            System.out.println("PreparedStatement + batch (biên dịch 1 lần): " + preparedMs + " ms");
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP TABLE IF EXISTS SanPhamTam");
            }
        }
    }

    private static void list(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            print(rs);
        }
    }

    // In các dòng và trả về số dòng đã in
    private static int print(ResultSet rs) throws SQLException {
        int count = 0;
        while (rs.next()) {
            count++;
            System.out.println("    %-6s %-30s %,12.0f VND  SL = %d".formatted(
                    rs.getString("maSP"), rs.getString("tenSP"), rs.getDouble("gia"), rs.getInt("soluong")));
        }
        return count;
    }
}
