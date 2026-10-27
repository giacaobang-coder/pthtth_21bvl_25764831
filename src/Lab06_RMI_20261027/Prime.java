package Lab06_RMI_20261027;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Prime extends UnicastRemoteObject implements PrimeItf {
    private static final long serialVersionUID = 1L;

    public Prime() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrime(int n) throws RemoteException {
        System.out.println("isPrime(" + n + ")");
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;
        // i <= n / i thay cho i * i <= n để không bị tràn số khi n gần Integer.MAX_VALUE
        for (int i = 3; i <= n / i; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
