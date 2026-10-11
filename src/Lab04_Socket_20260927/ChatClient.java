package Lab04_Socket_20260927;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5006;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // Luồng riêng để nhận tin nhắn của người khác trong lúc đang gõ.
            Thread receiver = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    // socket đã đóng
                }
                System.out.println("Đã ngắt kết nối với server");
            });
            receiver.setDaemon(true);
            receiver.start();

            System.out.println("Sau khi vào phòng dùng: USERS | MSG nội dung | QUIT");
            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    receiver.join(1000);
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
