package Lab06_RMI_20261027;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ChatServer {
    private static final int PORT = 1103;

    public static void main(String[] args) {
        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            LocateRegistry.createRegistry(PORT);
            Chat obj = new Chat();
            Naming.rebind("rmi://localhost:" + PORT + "/ChatObject", obj);
            System.out.println("ChatObject is registered on port " + PORT);
            System.out.println("Chờ client nhắn tin, gõ phản hồi rồi Enter để gửi.");

            String line;
            while ((line = console.readLine()) != null) {
                obj.reply(line);
            }
        } catch (Exception e) {
            System.err.println("Không khởi động được server: " + e);
        }
    }
}
