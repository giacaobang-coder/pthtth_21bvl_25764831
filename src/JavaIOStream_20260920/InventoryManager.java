package JavaIOStream_20260920;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private static final Path INVENTORY_FILE = Path.of("data", "JavaIOStream_20260920", "inventory.csv");
    private static final Path REPORT_FILE = Path.of("data", "JavaIOStream_20260920", "inventory-report.txt");

    public static void main(String[] args) {
        List<Product> entered = readProductsFromConsole();
        saveToCsv(entered, INVENTORY_FILE);

        List<Product> loaded = loadFromCsv(INVENTORY_FILE);
        displayProducts(loaded);
        writeReport(loaded, REPORT_FILE);
    }

    private static List<Product> readProductsFromConsole() {
        List<Product> products = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("Nhập danh sách sản phẩm. Gõ 'done' ở Mã sản phẩm để kết thúc.");
        try {
            while (true) {
                System.out.print("Mã sản phẩm: ");
                String code = reader.readLine();
                if (code == null || code.equalsIgnoreCase("done")) {
                    break;
                }
                System.out.print("Tên sản phẩm: ");
                String name = reader.readLine();
                System.out.print("Đơn giá: ");
                String priceStr = reader.readLine();
                System.out.print("Số lượng: ");
                String qtyStr = reader.readLine();

                if (name == null || priceStr == null || qtyStr == null) {
                    System.err.println("Dữ liệu nhập bị thiếu (EOF), dừng nhập.");
                    break;
                }

                try {
                    double price = Double.parseDouble(priceStr.trim());
                    int qty = Integer.parseInt(qtyStr.trim());
                    products.add(new Product(code.trim(), name.trim(), price, qty));
                    System.out.println("Đã thêm sản phẩm " + code.trim());
                } catch (NumberFormatException e) {
                    System.err.println("Sản phẩm '" + code + "': dữ liệu số không hợp lệ - " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Sản phẩm '" + code + "' bị từ chối: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không thể đọc dữ liệu từ bàn phím: " + e.getMessage());
        }
        return products;
    }

    private static void saveToCsv(List<Product> products, Path file) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                writer.write("ma,ten,donGia,soLuong");
                writer.newLine();
                for (Product p : products) {
                    writer.write("%s,%s,%s,%d".formatted(
                            p.getCode(), p.getName(), p.getUnitPrice(), p.getQuantity()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Không ghi được tệp " + file + ": " + e.getMessage());
        }
    }

    private static List<Product> loadFromCsv(Path file) {
        List<Product> products = new ArrayList<>();
        if (!Files.exists(file)) {
            System.err.println("Không tìm thấy tệp " + file);
            return products;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine(); // bỏ qua dòng tiêu đề
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    System.err.println("Tệp " + file + ", dòng " + lineNumber + " thiếu cột, đã bỏ qua");
                    continue;
                }
                try {
                    products.add(new Product(
                            parts[0].trim(), parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())));
                } catch (IllegalArgumentException e) {
                    System.err.println("Tệp " + file + ", dòng " + lineNumber
                            + " không hợp lệ: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không đọc được tệp " + file + ": " + e.getMessage());
        }
        return products;
    }

    private static void displayProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào.");
            return;
        }
        double total = 0;
        for (Product p : products) {
            System.out.println(p);
            total += p.inventoryValue();
        }
        System.out.printf("Tổng giá trị tồn kho: %,.0f VND%n", total);

        Product max = findMaxInventoryValue(products);
        if (max != null) {
            System.out.println("Sản phẩm có giá trị tồn kho cao nhất: " + max);
        }
    }

    private static Product findMaxInventoryValue(List<Product> products) {
        Product max = null;
        for (Product p : products) {
            if (max == null || p.inventoryValue() > max.inventoryValue()) {
                max = p;
            }
        }
        return max;
    }

    private static void writeReport(List<Product> products, Path file) {
        double total = 0;
        for (Product p : products) {
            total += p.inventoryValue();
        }
        Product max = findMaxInventoryValue(products);

        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                writer.write("Số sản phẩm: " + products.size());
                writer.newLine();
                writer.write("Tổng giá trị tồn kho: %,.0f VND".formatted(total));
                writer.newLine();
                if (max != null) {
                    writer.write("Sản phẩm giá trị tồn kho cao nhất: " + max);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Không ghi được báo cáo " + file + ": " + e.getMessage());
        }
    }
}
