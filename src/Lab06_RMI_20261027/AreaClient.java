package Lab06_RMI_20261027;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;

public class AreaClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String areaURL = "rmi://" + host + ":1105/AreaObject";

        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            AreaItf object = (AreaItf) Naming.lookup(areaURL);

            while (true) {
                System.out.println();
                System.out.println("===== TÍNH DIỆN TÍCH =====");
                System.out.println("1. Hình chữ nhật");
                System.out.println("2. Hình tròn");
                System.out.println("3. Hình tam giác");
                System.out.println("0. Thoát");
                System.out.print("Chọn: ");
                String choice = console.readLine();
                if (choice == null || choice.trim().equals("0")) break;

                try {
                    switch (choice.trim()) {
                        case "1": {
                            Double w = readDouble(console, "Chiều rộng: ");
                            if (w == null) break;
                            Double h = readDouble(console, "Chiều dài: ");
                            if (h == null) break;
                            System.out.println("Server: diện tích hình chữ nhật = %.2f".formatted(
                                    object.rectangleArea(w, h)));
                            break;
                        }
                        case "2": {
                            Double r = readDouble(console, "Bán kính: ");
                            if (r == null) break;
                            System.out.println("Server: diện tích hình tròn = %.2f".formatted(
                                    object.circleArea(r)));
                            break;
                        }
                        case "3": {
                            Double b = readDouble(console, "Cạnh đáy: ");
                            if (b == null) break;
                            Double h = readDouble(console, "Chiều cao: ");
                            if (h == null) break;
                            System.out.println("Server: diện tích hình tam giác = %.2f".formatted(
                                    object.triangleArea(b, h)));
                            break;
                        }
                        default:
                            System.err.println("Lựa chọn không hợp lệ");
                    }
                } catch (IllegalArgumentException e) {
                    // Lỗi kiểm tra dữ liệu ném ra ở server được RMI gửi nguyên về client
                    System.err.println("Server từ chối: " + e.getMessage());
                }
            }
            System.out.println("Tạm biệt!");
        } catch (Exception e) {
            System.err.println("Lỗi client: " + e);
        }
    }

    // Trả về null khi nhập sai định dạng, để quay lại menu
    private static Double readDouble(BufferedReader console, String prompt) throws IOException {
        System.out.print(prompt);
        String line = console.readLine();
        if (line == null) return null;
        try {
            return Double.parseDouble(line.trim());
        } catch (NumberFormatException e) {
            System.err.println("\"" + line.trim() + "\" không phải số hợp lệ");
            return null;
        }
    }
}
