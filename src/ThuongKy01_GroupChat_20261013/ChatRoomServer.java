package ThuongKy01_GroupChat_20261013;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomServer {
    private static final int PORT = 5100;
    private static final String[] ROOMS = {"Study", "Game", "TraiNghiem", "Music"};
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Chat room server listening on port " + PORT);
            System.out.println("Các phòng: " + String.join(", ", ROOMS));
            while (true) {
                Socket socket = server.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        }
    }

    public static String[] getRooms() {
        return ROOMS;
    }

    public static String findRoom(String choice) {
        if (choice.matches("\\d+")) {
            int index = Integer.parseInt(choice);
            if (index >= 1 && index <= ROOMS.length) {
                return ROOMS[index - 1];
            }
            return null;
        }
        for (String room : ROOMS) {
            if (room.equalsIgnoreCase(choice)) {
                return room;
            }
        }
        return null;
    }

    public static synchronized boolean addClient(ClientHandler client) {
        for (ClientHandler other : clients) {
            if (other.getClientName().equalsIgnoreCase(client.getClientName())) {
                return false;
            }
        }
        clients.add(client);
        return true;
    }

    public static synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static synchronized int countInRoom(String room) {
        int count = 0;
        for (ClientHandler client : clients) {
            if (room.equals(client.getRoom())) {
                count++;
            }
        }
        return count;
    }

    public static synchronized void broadcast(String room, ClientHandler sender, String message) {
        for (ClientHandler client : clients) {
            if (client != sender && room.equals(client.getRoom())) {
                client.send("[" + room + "] " + message);
            }
        }
    }
}
