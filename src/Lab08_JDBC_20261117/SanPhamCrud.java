package Lab08_JDBC_20261117;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài 1 - Quản lý danh mục sản phẩm (CRUD) trên bảng SanPham(maSP, tenSP, gia, soluong).
 * Dùng Statement nối chuỗi như phần hướng dẫn; bài 4 chuyển sang PreparedStatement.
 */
public class SanPhamCrud {
    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of(SanPhamSchema.DB_DIR));
            Class.forName("org.sqlite.JDBC");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Không khởi động được: " + e);
            return;
        }

        try (Connection conn = DriverManager.getConnection(SanPhamSchema.URL);
             Statement stmt = conn.createStatement();
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            stmt.executeUpdate(SanPhamSchema.CREATE_TABLE);
            System.out.println("Đã kết nối " + SanPhamSchema.URL);

            while (true) {
                System.out.println();
                System.out.println("===== QUẢN LÝ SẢN PHẨM =====");
                System.out.println("1. Thêm sản phẩm");
                System.out.println("2. Liệt kê tất cả");
                System.out.println("3. Tìm theo tên");
                System.out.println("4. Cập nhật giá");
                System.out.println("5. Cập nhật số lượng");
                System.out.println("6. Xóa sản phẩm");
                System.out.println("0. Thoát");
                System.out.print("Chọn: ");
                String choice = console.readLine();
                if (choice == null || choice.trim().equals("0")) break;

                try {
                    switch (choice.trim()) {
                        case "1": add(console, stmt); break;
                        case "2": list(stmt, "SELECT * FROM SanPham ORDER BY maSP"); break;
                        case "3": {
                            String keyword = ask(console, "Tên cần tìm: ");
                            list(stmt, "SELECT * FROM SanPham WHERE tenSP LIKE '%" + keyword + "%' ORDER BY maSP");
                            break;
                        }
                        case "4": update(console, stmt, "gia", "Giá mới: "); break;
                        case "5": update(console, stmt, "soluong", "Số lượng mới: "); break;
                        case "6": delete(console, stmt); break;
                        default: System.err.println("Lựa chọn không hợp lệ");
                    }
                } catch (SQLException e) {
                    System.err.println("Lỗi SQL: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.err.println("Giá và số lượng phải là số");
                }
            }
            System.out.println("Tạm biệt!");
        } catch (SQLException | IOException e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }

    private static void add(BufferedReader console, Statement stmt) throws IOException, SQLException {
        String ma = ask(console, "Mã SP: ");
        // Báo trùng ngay, không bắt nhập hết rồi mới để INSERT vi phạm PRIMARY KEY
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM SanPham WHERE maSP = '" + ma + "'")) {
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("Mã " + ma + " đã tồn tại, hãy dùng chức năng cập nhật.");
                return;
            }
        }
        String ten = ask(console, "Tên SP: ");
        double gia = Double.parseDouble(ask(console, "Giá: "));
        int soLuong = Integer.parseInt(ask(console, "Số lượng: "));
        String sql = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES ('"
                + ma + "', '" + ten + "', " + gia + ", " + soLuong + ")";
        stmt.executeUpdate(sql);
        System.out.println("Đã thêm " + ma + " - " + ten);
    }

    private static void list(Statement stmt, String sql) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(sql)) {
            int count = 0;
            System.out.println("%-6s %-25s %15s %8s".formatted("Mã", "Tên", "Giá (VND)", "SL"));
            while (rs.next()) {
                count++;
                System.out.println("%-6s %-25s %,15.0f %8d".formatted(
                        rs.getString("maSP"), rs.getString("tenSP"),
                        rs.getDouble("gia"), rs.getInt("soluong")));
            }
            System.out.println("(" + count + " sản phẩm)");
        }
    }

    private static void update(BufferedReader console, Statement stmt, String column, String prompt)
            throws IOException, SQLException {
        String ma = ask(console, "Mã SP cần cập nhật: ");
        String value = ask(console, prompt);
        Number number = column.equals("gia") ? Double.parseDouble(value) : Integer.parseInt(value);
        int rows = stmt.executeUpdate("UPDATE SanPham SET " + column + " = " + number
                + " WHERE maSP = '" + ma + "'");
        System.out.println(rows > 0 ? "Đã cập nhật " + ma : "Không tìm thấy mã " + ma);
    }

    private static void delete(BufferedReader console, Statement stmt) throws IOException, SQLException {
        String ma = ask(console, "Mã SP cần xóa: ");
        int rows = stmt.executeUpdate("DELETE FROM SanPham WHERE maSP = '" + ma + "'");
        System.out.println(rows > 0 ? "Đã xóa " + ma : "Không tìm thấy mã " + ma);
    }

    private static String ask(BufferedReader console, String prompt) throws IOException {
        System.out.print(prompt);
        String line = console.readLine();
        return line == null ? "" : line.trim();
    }
}
