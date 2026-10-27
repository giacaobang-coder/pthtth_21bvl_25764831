package Lab06_RMI_20261027;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bài 4 - Quản lý danh bạ từ xa.
 * addContact thêm mới hoặc ghi đè số của tên đã có (dùng cho cả chức năng sửa).
 * findContact trả về số điện thoại, hoặc null nếu không có tên đó.
 */
public interface PhoneBookItf extends Remote {
    void addContact(String name, String phone) throws RemoteException;

    String findContact(String name) throws RemoteException;

    boolean deleteContact(String name) throws RemoteException;
}
