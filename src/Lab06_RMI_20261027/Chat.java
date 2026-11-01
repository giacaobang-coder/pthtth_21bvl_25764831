package Lab06_RMI_20261027;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Chat extends UnicastRemoteObject implements ChatItf {
    private static final long serialVersionUID = 1L;

    // Phản hồi do người ở server gõ, chờ client lấy về
    private final BlockingQueue<String> replies = new LinkedBlockingQueue<>();

    public Chat() throws RemoteException {
        super();
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        System.out.println("Client: " + message);
        System.out.print("Server: ");
    }

    @Override
    public String receiveMessage() throws RemoteException {
        try {
            return replies.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Server bị ngắt khi đang chờ phản hồi", e);
        }
    }

    // Gọi cục bộ từ ChatServer, không gọi từ xa
    public void reply(String message) {
        replies.add(message);
    }
}
