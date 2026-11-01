package Lab06_RMI_20261027;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bài 6 - Quản lý tài khoản ngân hàng từ xa.
 * withdraw trả về false khi số dư không đủ.
 */
public interface AccountItf extends Remote {
    double getBalance() throws RemoteException;

    void deposit(double amount) throws RemoteException;

    boolean withdraw(double amount) throws RemoteException;
}
