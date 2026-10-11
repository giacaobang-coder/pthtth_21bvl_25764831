package Lab04_Socket_20260927;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Bài 4 - Máy tính từ xa.
 * Giao thức: CALC toán_tử toán_hạng_1 toán_hạng_2  ->  OK kết_quả | ERR mã_lỗi
 */
public class CalcServer {
    private static final int PORT = 5005;
    private static final int MAX_CLIENTS = 10;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Calc server listening on port " + PORT);
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> {
                    try (socket) {
                        serve(socket);
                    } catch (IOException e) {
                        System.err.println("Lỗi phiên client: " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        } finally {
            pool.shutdown();
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
                if (request.trim().equalsIgnoreCase("QUIT")) break;
            }
        }
    }

    static String process(String request) {
        String[] parts = request.trim().split("\\s+");
        if (parts[0].equalsIgnoreCase("QUIT")) return "OK BYE";
        if (!parts[0].equalsIgnoreCase("CALC")) return "ERR UNKNOWN_COMMAND";
        if (parts.length != 4) return "ERR INVALID_FORMAT";

        String op = parts[1];
        if (!op.matches("[+\\-*/]")) return "ERR UNSUPPORTED_OPERATOR";

        BigDecimal a;
        BigDecimal b;
        try {
            a = new BigDecimal(parts[2]);
            b = new BigDecimal(parts[3]);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        BigDecimal result;
        switch (op) {
            case "+": result = a.add(b); break;
            case "-": result = a.subtract(b); break;
            case "*": result = a.multiply(b); break;
            default:
                if (b.signum() == 0) return "ERR DIVIDE_BY_ZERO";
                result = a.divide(b, MathContext.DECIMAL64);
        }
        BigDecimal clean = result.signum() == 0 ? BigDecimal.ZERO : result.stripTrailingZeros();
        return "OK " + clean.toPlainString();
    }
}
