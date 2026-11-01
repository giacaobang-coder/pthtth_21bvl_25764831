package Lab06_RMI_20261027;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;

public class ChatClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String chatURL = "rmi://" + host + ":1103/ChatObject";

        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            ChatItf object = (ChatItf) Naming.lookup(chatURL);
            System.out.println("Đã kết nối server. Nhập tin nhắn, nhập QUIT để thoát.");

            while (true) {
                System.out.print("Bạn: ");
                String line = console.readLine();
                if (line == null || line.trim().equalsIgnoreCase("QUIT")) break;
                if (line.isBlank()) continue;
                object.sendMessage(line);
                System.out.println("Server: " + object.receiveMessage());
            }
            System.out.println("Tạm biệt!");
        } catch (Exception e) {
            System.err.println("Lỗi client: " + e);
        }
    }
}
