package Lab04_Socket_20260927;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitToWordServer {
    private static final int PORT = 5002;
    private static final String[] WORDS = {
            "không", "một", "hai", "ba", "bốn",
            "năm", "sáu", "bảy", "tám", "chín"
    };

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("DigitToWord server listening on port " + PORT);
            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi phiên client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        }
    }

    private static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                if (request.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                out.println(process(request));
            }
        }
    }

    static String process(String request) {
        if (request.length() != 1 || !Character.isDigit(request.charAt(0))) {
            return "ERR INVALID_DIGIT";
        }
        int digit = request.charAt(0) - '0';
        return "OK " + WORDS[digit];
    }
}
