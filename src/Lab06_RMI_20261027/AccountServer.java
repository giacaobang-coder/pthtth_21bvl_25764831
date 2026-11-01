package Lab06_RMI_20261027;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class AccountServer {
    private static final int PORT = 1104;
    private static final double INITIAL_BALANCE = 1_000_000;

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(PORT);
            Account obj = new Account(INITIAL_BALANCE);
            Naming.rebind("rmi://localhost:" + PORT + "/AccountObject", obj);
            System.out.println("AccountObject is registered on port " + PORT);
            System.out.println("Số dư ban đầu: %,.0f VND".formatted(INITIAL_BALANCE));
        } catch (Exception e) {
            System.err.println("Không khởi động được server: " + e);
        }
    }
}
