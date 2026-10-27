package Lab06_RMI_20261027;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class PhoneBook extends UnicastRemoteObject implements PhoneBookItf {
    private static final long serialVersionUID = 1L;

    // RMI phục vụ mỗi lời gọi trên một luồng riêng nên các phương thức đều synchronized
    private final HashMap<String, String> contacts = new HashMap<>();

    public PhoneBook() throws RemoteException {
        super();
    }

    @Override
    public synchronized void addContact(String name, String phone) throws RemoteException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên không được rỗng");
        }
        if (phone == null || !phone.trim().matches("\\+?\\d{8,15}")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 8-15 chữ số");
        }
        contacts.put(name.trim(), phone.trim());
        System.out.println("addContact: " + name.trim() + " - " + phone.trim());
    }

    @Override
    public synchronized String findContact(String name) throws RemoteException {
        System.out.println("findContact: " + name);
        return name == null ? null : contacts.get(name.trim());
    }

    @Override
    public synchronized boolean deleteContact(String name) throws RemoteException {
        System.out.println("deleteContact: " + name);
        return name != null && contacts.remove(name.trim()) != null;
    }
}
