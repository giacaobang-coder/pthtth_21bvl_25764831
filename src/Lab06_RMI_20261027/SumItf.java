package Lab06_RMI_20261027;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bài 2 - Tính tổng hai số nguyên a + b trên server.
 */
public interface SumItf extends Remote {
    int add(int a, int b) throws RemoteException;
}
