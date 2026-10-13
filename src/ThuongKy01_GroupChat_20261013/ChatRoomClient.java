package ThuongKy01_GroupChat_20261013;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatRoomClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5100;
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

            Thread receiver = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi nhận dữ liệu: " + e.getMessage());
                }
                System.out.println("[Đã ngắt kết nối với server]");
                System.exit(0);
            });
            receiver.setDaemon(true);
            receiver.start();

            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);
                if (line.trim().equalsIgnoreCase("/exit")) {
                    receiver.join(1500);
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Không kết nối được tới server " + host + ":" + port + " - " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
