package Lab06_RMI_20261027;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;

public class SumClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String sumURL = "rmi://" + host + ":1100/SumObject";

        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            SumItf object = (SumItf) Naming.lookup(sumURL);
            System.out.println("Nhập hai số nguyên a và b. Nhập QUIT để thoát.");

            while (true) {
                Integer a = readInt(console, "a = ");
                if (a == null) break;
                Integer b = readInt(console, "b = ");
                if (b == null) break;
                try {
                    System.out.println("Server: " + a + " + " + b + " = " + object.add(a, b));
                } catch (ArithmeticException e) {
                    System.err.println("Tổng vượt quá giới hạn kiểu int");
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi client: " + e);
        }
    }

    // Trả về null khi người dùng nhập QUIT hoặc hết dữ liệu vào
    private static Integer readInt(BufferedReader console, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String line = console.readLine();
            if (line == null || line.trim().equalsIgnoreCase("QUIT")) {
                return null;
            }
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.err.println("\"" + line.trim() + "\" không phải số nguyên, nhập lại.");
            }
        }
    }
}
