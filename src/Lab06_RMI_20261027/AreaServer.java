package Lab06_RMI_20261027;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class AreaServer {
    private static final int PORT = 1105;

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(PORT);
            Area obj = new Area();
            Naming.rebind("rmi://localhost:" + PORT + "/AreaObject", obj);
            System.out.println("AreaObject is registered on port " + PORT);
        } catch (Exception e) {
            System.err.println("Không khởi động được server: " + e);
        }
    }
}
