package Lab04_Socket_20260927;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Bài 6 - Thực nghiệm so sánh TCP và UDP trên localhost.
 * Chương trình tự chạy echo server TCP (cổng 5007) và UDP (cổng 5008) trong cùng tiến trình,
 * gửi MESSAGES thông điệp kích thước cố định theo kiểu hỏi - đáp, lặp lại ROUNDS lần.
 * Tham số tùy chọn: [số lần lặp] [số thông điệp] [kích thước byte] [timeout UDP ms]
 */
public class TcpUdpBenchmark {
    private static final int TCP_PORT = 5007;
    private static final int UDP_PORT = 5008;

    public static void main(String[] args) throws Exception {
        int rounds = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        int messages = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
        int size = args.length > 2 ? Integer.parseInt(args[2]) : 100;
        int timeoutMs = args.length > 3 ? Integer.parseInt(args[3]) : 500;

        startTcpEchoServer();
        startUdpEchoServer();
        Thread.sleep(300);

        String payload = "x".repeat(size);

        System.out.println("== Môi trường và phương pháp đo ==");
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")");
        System.out.println("CPU: " + Runtime.getRuntime().availableProcessors() + " nhân");
        System.out.println("Mạng: localhost (loopback), server và client cùng một máy");
        System.out.println("Số thông điệp mỗi lần: " + messages + ", kích thước: " + size + " byte");
        System.out.println("Số lần lặp: " + rounds + ", timeout UDP: " + timeoutMs + " ms");
        System.out.println("Phương pháp: gửi một thông điệp, chờ phản hồi rồi mới gửi tiếp; "
                + "đo bằng System.nanoTime(); chạy 1 lần khởi động bỏ qua kết quả.");
        System.out.println();

        runTcp(messages, payload);   // khởi động JIT, không tính
        runUdp(messages, payload, timeoutMs);

        long[] tcpTime = new long[rounds];
        long[] udpTime = new long[rounds];
        int[] tcpOk = new int[rounds];
        int[] udpOk = new int[rounds];

        System.out.printf("%-6s | %-22s | %-22s%n", "Lần", "TCP (ms / nhận)", "UDP (ms / nhận)");
        System.out.println("-------+------------------------+-----------------------");
        for (int i = 0; i < rounds; i++) {
            long t = System.nanoTime();
            tcpOk[i] = runTcp(messages, payload);
            tcpTime[i] = (System.nanoTime() - t) / 1_000_000;

            t = System.nanoTime();
            udpOk[i] = runUdp(messages, payload, timeoutMs);
            udpTime[i] = (System.nanoTime() - t) / 1_000_000;

            System.out.printf("%-6d | %6d ms / %4d/%-4d | %6d ms / %4d/%-4d%n",
                    i + 1, tcpTime[i], tcpOk[i], messages, udpTime[i], udpOk[i], messages);
        }

        System.out.println();
        System.out.printf("TCP: trung bình %.1f ms, min %d ms, max %d ms%n",
                avg(tcpTime), Arrays.stream(tcpTime).min().getAsLong(), Arrays.stream(tcpTime).max().getAsLong());
        System.out.printf("UDP: trung bình %.1f ms, min %d ms, max %d ms%n",
                avg(udpTime), Arrays.stream(udpTime).min().getAsLong(), Arrays.stream(udpTime).max().getAsLong());
        System.out.println("Tổng phản hồi nhận được - TCP: " + Arrays.stream(tcpOk).sum() + "/" + (messages * rounds)
                + ", UDP: " + Arrays.stream(udpOk).sum() + "/" + (messages * rounds));
        System.exit(0);
    }

    private static double avg(long[] values) {
        return Arrays.stream(values).average().orElse(0);
    }

    /** Trả về số phản hồi nhận được. */
    private static int runTcp(int messages, String payload) throws IOException {
        int received = 0;
        try (Socket socket = new Socket("localhost", TCP_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            for (int i = 0; i < messages; i++) {
                out.println(payload);
                if (in.readLine() != null) received++;
            }
        }
        return received;
    }

    private static int runUdp(int messages, String payload, int timeoutMs) throws IOException {
        int received = 0;
        byte[] data = payload.getBytes(StandardCharsets.UTF_8);
        InetAddress server = InetAddress.getByName("localhost");
        byte[] buffer = new byte[data.length + 16];
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeoutMs);
            for (int i = 0; i < messages; i++) {
                socket.send(new DatagramPacket(data, data.length, server, UDP_PORT));
                try {
                    socket.receive(new DatagramPacket(buffer, buffer.length));
                    received++;
                } catch (SocketTimeoutException e) {
                    // gói bị mất: không gửi lại, chỉ ghi nhận thiếu phản hồi
                }
            }
        }
        return received;
    }

    private static void startTcpEchoServer() throws IOException {
        ServerSocket server = new ServerSocket(TCP_PORT);
        Thread acceptor = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = server.accept();
                    Thread t = new Thread(() -> {
                        try (socket;
                             BufferedReader in = new BufferedReader(new InputStreamReader(
                                     socket.getInputStream(), StandardCharsets.UTF_8));
                             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                            String line;
                            while ((line = in.readLine()) != null) out.println(line);
                        } catch (IOException e) {
                            // client đóng kết nối
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                } catch (IOException e) {
                    return;
                }
            }
        });
        acceptor.setDaemon(true);
        acceptor.start();
    }

    private static void startUdpEchoServer() throws IOException {
        DatagramSocket socket = new DatagramSocket(UDP_PORT);
        Thread t = new Thread(() -> {
            byte[] buffer = new byte[4096];
            while (true) {
                try {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    socket.send(new DatagramPacket(request.getData(), request.getOffset(),
                            request.getLength(), request.getAddress(), request.getPort()));
                } catch (IOException e) {
                    return;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
