package Lab04_Socket_20260927;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeServiceTcpServer {
    private static final int PORT = 5003;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH mm ss");

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("DateTime TCP server listening on port " + PORT);
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
                out.println(process(request));
                if (request.equalsIgnoreCase("QUIT")) break;
            }
        }
    }

    static String process(String request) {
        LocalDateTime now = LocalDateTime.now();
        String trimmed = request.trim();
        if (trimmed.equalsIgnoreCase("DATE")) return "OK " + now.format(DATE_FORMAT);
        if (trimmed.equalsIgnoreCase("TIME")) return "OK " + now.format(TIME_FORMAT);
        if (trimmed.equalsIgnoreCase("DATETIME")) {
            return "OK " + now.format(DATE_FORMAT) + " " + now.format(TIME_FORMAT);
        }
        if (trimmed.equalsIgnoreCase("QUIT")) return "OK BYE";
        return "ERR UNKNOWN_COMMAND";
    }
}
