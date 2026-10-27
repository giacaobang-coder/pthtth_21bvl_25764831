package Lab06_RMI_20261027;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class PhoneBookServer {
    private static final int PORT = 1102;

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(PORT);
            PhoneBook obj = new PhoneBook();
            Naming.rebind("rmi://localhost:" + PORT + "/PhoneBookObject", obj);
            System.out.println("PhoneBookObject is registered on port " + PORT);
        } catch (Exception e) {
            System.err.println("Không khởi động được server: " + e);
        }
    }
}
