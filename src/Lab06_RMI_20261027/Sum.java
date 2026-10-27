package Lab06_RMI_20261027;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Sum extends UnicastRemoteObject implements SumItf {
    private static final long serialVersionUID = 1L;

    public Sum() throws RemoteException {
        super();
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        System.out.println("add(" + a + ", " + b + ")");
        // addExact ném ArithmeticException khi tràn số, lỗi này được gửi về client
        return Math.addExact(a, b);
    }
}
