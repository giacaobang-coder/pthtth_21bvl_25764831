package Lab06_RMI_20261027;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class PrimeServer {
    private static final int PORT = 1101;

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(PORT);
            Prime obj = new Prime();
            Naming.rebind("rmi://localhost:" + PORT + "/PrimeObject", obj);
            System.out.println("PrimeObject is registered on port " + PORT);
        } catch (Exception e) {
            System.err.println("Không khởi động được server: " + e);
        }
    }
}
