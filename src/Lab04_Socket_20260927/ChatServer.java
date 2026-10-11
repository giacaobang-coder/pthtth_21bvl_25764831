package Lab04_Socket_20260927;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Bài 5 - Chat TCP nhiều client.
 * Client gửi nickname đầu tiên, sau đó dùng USERS, MSG nội_dung, QUIT.
 */
public class ChatServer {
    private static final int PORT = 5006;
    private static final int MAX_CLIENTS = 20;
    private static final Map<String, PrintWriter> CLIENTS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Chat server listening on port " + PORT);
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handle(socket));
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handle(Socket socket) {
        String remote = String.valueOf(socket.getRemoteSocketAddress());
        String nick = null;
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println("INFO Nhập nickname (chữ, số, - hoặc _):");
            String line;
            while (nick == null && (line = in.readLine()) != null) {
                String candidate = line.trim();
                if (!candidate.matches("[\\p{L}0-9_-]{1,20}")) {
                    out.println("ERR INVALID_NICKNAME");
                } else if (CLIENTS.putIfAbsent(candidate, out) != null) {
                    out.println("ERR NICKNAME_TAKEN");
                } else {
                    nick = candidate;
                }
            }
            if (nick == null) return;

            System.out.println("Joined: " + nick + " (" + remote + ")");
            out.println("OK WELCOME " + nick);
            broadcast(nick, "INFO " + nick + " đã tham gia");

            while ((line = in.readLine()) != null) {
                String cmd = line.trim();
                if (cmd.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                } else if (cmd.equalsIgnoreCase("USERS")) {
                    out.println("USERS " + String.join(", ", new TreeSet<>(CLIENTS.keySet())));
                } else if (cmd.regionMatches(true, 0, "MSG ", 0, 4) && cmd.length() > 4) {
                    broadcast(nick, "MSG " + nick + ": " + cmd.substring(4).trim());
                    out.println("OK SENT");
                } else {
                    out.println("ERR UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Client " + remote + " ngắt bất thường: " + e.getMessage());
        } finally {
            if (nick != null) {
                CLIENTS.remove(nick);
                System.out.println("Left: " + nick + " (" + remote + ")");
                broadcast(nick, "INFO " + nick + " đã rời phòng");
            }
        }
    }

    /** Gửi cho tất cả client trừ người gửi; PrintWriter tự đồng bộ nên không bị lẫn dòng. */
    private static void broadcast(String sender, String message) {
        for (Map.Entry<String, PrintWriter> entry : CLIENTS.entrySet()) {
            if (!entry.getKey().equals(sender)) {
                entry.getValue().println(message);
            }
        }
    }
}
