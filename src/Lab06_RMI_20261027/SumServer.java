package Lab06_RMI_20261027;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class SumServer {
    private static final int PORT = 1100;

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(PORT);
            Sum obj = new Sum();
            Naming.rebind("rmi://localhost:" + PORT + "/SumObject", obj);
            System.out.println("SumObject is registered on port " + PORT);
        } catch (Exception e) {
            System.err.println("Không khởi động được server: " + e);
        }
    }
}
