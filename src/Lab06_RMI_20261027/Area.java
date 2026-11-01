package Lab06_RMI_20261027;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Area extends UnicastRemoteObject implements AreaItf {
    private static final long serialVersionUID = 1L;

    public Area() throws RemoteException {
        super();
    }

    @Override
    public double rectangleArea(double width, double height) throws RemoteException {
        System.out.println("rectangleArea(" + width + ", " + height + ")");
        checkPositive(width, "Chiều rộng");
        checkPositive(height, "Chiều dài");
        return width * height;
    }

    @Override
    public double circleArea(double radius) throws RemoteException {
        System.out.println("circleArea(" + radius + ")");
        checkPositive(radius, "Bán kính");
        return Math.PI * radius * radius;
    }

    @Override
    public double triangleArea(double base, double height) throws RemoteException {
        System.out.println("triangleArea(" + base + ", " + height + ")");
        checkPositive(base, "Cạnh đáy");
        checkPositive(height, "Chiều cao");
        return base * height / 2;
    }

    // !(value > 0) để chặn cả NaN, vì NaN <= 0 cũng cho kết quả false
    private static void checkPositive(double value, String name) {
        if (!(value > 0) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(name + " phải là số dương");
        }
    }
}
