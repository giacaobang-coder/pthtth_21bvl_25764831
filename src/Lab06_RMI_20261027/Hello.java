package Lab06_RMI_20261027;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Hello extends UnicastRemoteObject implements HelloItf {
    private static final long serialVersionUID = 1L;

    public Hello() throws RemoteException {
        super();
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello, World!";
    }
}
