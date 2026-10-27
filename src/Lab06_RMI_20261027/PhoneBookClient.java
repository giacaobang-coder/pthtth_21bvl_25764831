package Lab06_RMI_20261027;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;

public class PhoneBookClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String phoneBookURL = "rmi://" + host + ":1102/PhoneBookObject";

        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            PhoneBookItf object = (PhoneBookItf) Naming.lookup(phoneBookURL);

            while (true) {
                System.out.println();
                System.out.println("===== QUẢN LÝ DANH BẠ =====");
                System.out.println("1. Thêm liên hệ");
                System.out.println("2. Sửa số điện thoại");
                System.out.println("3. Xóa liên hệ");
                System.out.println("4. Tìm kiếm liên hệ");
                System.out.println("0. Thoát");
                System.out.print("Chọn: ");
                String choice = console.readLine();
                if (choice == null || choice.trim().equals("0")) break;

                try {
                    switch (choice.trim()) {
                        case "1": add(console, object); break;
                        case "2": update(console, object); break;
                        case "3": delete(console, object); break;
                        case "4": find(console, object); break;
                        default: System.err.println("Lựa chọn không hợp lệ");
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

    private static void add(BufferedReader console, PhoneBookItf object) throws IOException {
        String name = ask(console, "Tên: ");
        if (object.findContact(name) != null) {
            System.err.println("\"" + name + "\" đã có trong danh bạ, hãy dùng chức năng Sửa.");
            return;
        }
        String phone = ask(console, "Số điện thoại: ");
        object.addContact(name, phone);
        System.out.println("Đã thêm " + name + " - " + phone);
    }

    private static void update(BufferedReader console, PhoneBookItf object) throws IOException {
        String name = ask(console, "Tên cần sửa: ");
        String oldPhone = object.findContact(name);
        if (oldPhone == null) {
            System.err.println("Không tìm thấy \"" + name + "\"");
            return;
        }
        String phone = ask(console, "Số mới (số cũ " + oldPhone + "): ");
        object.addContact(name, phone);
        System.out.println("Đã cập nhật " + name + " - " + phone);
    }

    private static void delete(BufferedReader console, PhoneBookItf object) throws IOException {
        String name = ask(console, "Tên cần xóa: ");
        if (object.deleteContact(name)) {
            System.out.println("Đã xóa " + name);
        } else {
            System.err.println("Không tìm thấy \"" + name + "\"");
        }
    }

    private static void find(BufferedReader console, PhoneBookItf object) throws IOException {
        String name = ask(console, "Tên cần tìm: ");
        String phone = object.findContact(name);
        if (phone == null) {
            System.err.println("Không tìm thấy \"" + name + "\"");
        } else {
            System.out.println(name + " - " + phone);
        }
    }

    private static String ask(BufferedReader console, String prompt) throws IOException {
        System.out.print(prompt);
        String line = console.readLine();
        return line == null ? "" : line.trim();
    }
}
