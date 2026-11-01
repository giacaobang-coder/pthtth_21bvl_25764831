package Lab06_RMI_20261027;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bài 5 - Chat 1-1 giữa client và server.
 * sendMessage gửi tin nhắn lên server; receiveMessage chờ đến khi server gõ phản hồi.
 * (void sendMessage không trả về gì nên cần thêm receiveMessage để client nhận phản hồi.)
 */
public interface ChatItf extends Remote {
    void sendMessage(String message) throws RemoteException;

    String receiveMessage() throws RemoteException;
}
