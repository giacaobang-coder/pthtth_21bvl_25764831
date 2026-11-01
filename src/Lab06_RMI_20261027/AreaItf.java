package Lab06_RMI_20261027;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bài 7 - Tính diện tích hình chữ nhật, hình tròn, hình tam giác trên server.
 */
public interface AreaItf extends Remote {
    double rectangleArea(double width, double height) throws RemoteException;

    double circleArea(double radius) throws RemoteException;

    double triangleArea(double base, double height) throws RemoteException;
}
