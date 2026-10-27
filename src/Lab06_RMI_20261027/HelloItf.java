package Lab06_RMI_20261027;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bài 1 - Remote interface: client gọi sayHello() để nhận chuỗi "Hello, World!".
 */
public interface HelloItf extends Remote {
    String sayHello() throws RemoteException;
}
