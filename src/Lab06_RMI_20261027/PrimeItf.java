package Lab06_RMI_20261027;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bài 3 - Kiểm tra số nguyên tố trên server.
 */
public interface PrimeItf extends Remote {
    boolean isPrime(int n) throws RemoteException;
}
