package Lab06_RMI_20261027;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Account extends UnicastRemoteObject implements AccountItf {
    private static final long serialVersionUID = 1L;

    // Nhiều client có thể gửi/rút cùng lúc nên các phương thức đều synchronized
    private double balance;

    public Account(double initialBalance) throws RemoteException {
        super();
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Số dư ban đầu không được âm");
        }
        this.balance = initialBalance;
    }

    @Override
    public synchronized double getBalance() throws RemoteException {
        System.out.println("getBalance: " + "%,.0f".formatted(balance));
        return balance;
    }

    // !(amount > 0) để chặn cả NaN, vì NaN <= 0 cũng cho kết quả false
    private static boolean isInvalid(double amount) {
        return !(amount > 0) || Double.isInfinite(amount);
    }

    @Override
    public synchronized void deposit(double amount) throws RemoteException {
        if (isInvalid(amount)) {
            throw new IllegalArgumentException("Số tiền gửi phải lớn hơn 0");
        }
        balance += amount;
        System.out.println("deposit: +" + "%,.0f".formatted(amount) + " -> " + "%,.0f".formatted(balance));
    }

    @Override
    public synchronized boolean withdraw(double amount) throws RemoteException {
        if (isInvalid(amount)) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0");
        }
        if (amount > balance) {
            System.out.println("withdraw: -" + "%,.0f".formatted(amount) + " thất bại, không đủ số dư");
            return false;
        }
        balance -= amount;
        System.out.println("withdraw: -" + "%,.0f".formatted(amount) + " -> " + "%,.0f".formatted(balance));
        return true;
    }
}
