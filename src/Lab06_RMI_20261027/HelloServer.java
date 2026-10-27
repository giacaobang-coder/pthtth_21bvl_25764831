package Lab06_RMI_20261027;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class HelloServer {
    private static final int PORT = 1099;

    public static void main(String[] args) {
        try {
            // Tự khởi động rmiregistry trong server, không cần chạy lệnh rmiregistry riêng
            LocateRegistry.createRegistry(PORT);
            Hello obj = new Hello();
            Naming.rebind("rmi://localhost:" + PORT + "/HelloObject", obj);
            System.out.println("HelloObject is registered on port " + PORT);
        } catch (Exception e) {
            System.err.println("Không khởi động được server: " + e);
        }
    }
}
