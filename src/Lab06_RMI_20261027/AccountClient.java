package Lab06_RMI_20261027;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;

public class AccountClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String accountURL = "rmi://" + host + ":1104/AccountObject";

        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            AccountItf object = (AccountItf) Naming.lookup(accountURL);

            while (true) {
                System.out.println();
                System.out.println("===== TÀI KHOẢN NGÂN HÀNG =====");
                System.out.println("1. Xem số dư");
                System.out.println("2. Gửi tiền");
                System.out.println("3. Rút tiền");
                System.out.println("0. Thoát");
                System.out.print("Chọn: ");
                String choice = console.readLine();
                if (choice == null || choice.trim().equals("0")) break;

                try {
                    switch (choice.trim()) {
                        case "1":
                            System.out.println("Số dư: %,.0f VND".formatted(object.getBalance()));
                            break;
                        case "2": {
                            Double amount = readAmount(console, "Số tiền gửi: ");
                            if (amount == null) break;
                            object.deposit(amount);
                            System.out.println("Đã gửi %,.0f VND. Số dư: %,.0f VND".formatted(
                                    amount, object.getBalance()));
                            break;
                        }
                        case "3": {
                            Double amount = readAmount(console, "Số tiền rút: ");
                            if (amount == null) break;
                            if (object.withdraw(amount)) {
                                System.out.println("Đã rút %,.0f VND. Số dư: %,.0f VND".formatted(
                                        amount, object.getBalance()));
                            } else {
                                System.err.println("Không đủ số dư để rút %,.0f VND".formatted(amount));
                            }
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
    private static Double readAmount(BufferedReader console, String prompt) throws IOException {
        System.out.print(prompt);
        String line = console.readLine();
        if (line == null) return null;
        try {
            return Double.parseDouble(line.trim());
        } catch (NumberFormatException e) {
            System.err.println("\"" + line.trim() + "\" không phải số tiền hợp lệ");
            return null;
        }
    }
}
