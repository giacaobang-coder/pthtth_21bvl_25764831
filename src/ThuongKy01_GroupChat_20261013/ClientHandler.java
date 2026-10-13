package ThuongKy01_GroupChat_20261013;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends Thread {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name;
    private volatile String room;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public String getClientName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public void send(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        String remote = String.valueOf(socket.getRemoteSocketAddress());
        System.out.println("Connected: " + remote);
        try (socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            in = reader;
            out = writer;
            out.println("Chào mừng đến với Chat nhóm!");
            if (askName()) {
                chooseAndChat();
            }
        } catch (IOException e) {
            System.err.println("Client " + remote + " ngắt bất thường: " + e.getMessage());
        } finally {
            leaveRoom();
            ChatRoomServer.removeClient(this);
            System.out.println("Disconnected: " + remote);
        }
    }

    private boolean askName() throws IOException {
        while (true) {
            out.println("Nhập tên của bạn (chữ, số, - hoặc _, tối đa 20 ký tự):");
            String line = in.readLine();
            if (line == null) {
                return false;
            }
            String candidate = line.trim();
            if (candidate.equalsIgnoreCase("/exit")) {
                out.println("Tạm biệt!");
                return false;
            }
            if (!candidate.matches("[\\p{L}\\p{N}_-]{1,20}")) {
                out.println("Tên không hợp lệ, vui lòng nhập lại.");
                continue;
            }
            name = candidate;
            if (ChatRoomServer.addClient(this)) {
                out.println("Xin chào " + name + "!");
                System.out.println("Tên: " + name);
                return true;
            }
            name = null;
            out.println("Tên \"" + candidate + "\" đã có người dùng, vui lòng chọn tên khác.");
        }
    }

    private void chooseAndChat() throws IOException {
        while (true) {
            showRooms();
            String chosen = chooseRoom();
            if (chosen == null) {
                return;
            }
            if (!chat(chosen)) {
                return;
            }
        }
    }

    private void showRooms() {
        out.println();
        out.println("===== DANH SÁCH PHÒNG =====");
        String[] rooms = ChatRoomServer.getRooms();
        for (int i = 0; i < rooms.length; i++) {
            out.println("%d. %s (%d người)".formatted(
                    i + 1, rooms[i], ChatRoomServer.countInRoom(rooms[i])));
        }
        out.println("Chọn phòng bằng số hoặc tên phòng. Nhập /exit để ngắt kết nối:");
    }

    private String chooseRoom() throws IOException {
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return null;
            }
            String choice = line.trim();
            if (choice.equalsIgnoreCase("/exit")) {
                out.println("Tạm biệt " + name + "!");
                return null;
            }
            if (choice.equalsIgnoreCase("/quit")) {
                out.println("Bạn đang ở danh sách phòng. Chọn một phòng hoặc nhập /exit để thoát.");
                continue;
            }
            String found = ChatRoomServer.findRoom(choice);
            if (found != null) {
                return found;
            }
            out.println("Phòng \"" + choice + "\" không tồn tại. Hãy chọn lại.");
        }
    }

    private boolean chat(String chosen) throws IOException {
        room = chosen;
        System.out.println("[" + chosen + "] " + name + " đã vào phòng");
        out.println("Bạn đã vào phòng " + chosen + ". Gõ /quit để rời phòng, /exit để ngắt kết nối.");
        ChatRoomServer.broadcast(chosen, this, "* " + name + " đã vào phòng");

        String line;
        while ((line = in.readLine()) != null) {
            String text = line.trim();
            if (text.isEmpty()) {
                continue;
            }
            if (text.equalsIgnoreCase("/quit")) {
                leaveRoom();
                out.println("Bạn đã rời phòng " + chosen + ".");
                return true;
            }
            if (text.equalsIgnoreCase("/exit")) {
                leaveRoom();
                out.println("Tạm biệt " + name + "!");
                return false;
            }
            if (text.startsWith("/")) {
                out.println("Lệnh không hợp lệ. Dùng /quit để rời phòng hoặc /exit để ngắt kết nối.");
                continue;
            }
            ChatRoomServer.broadcast(chosen, this, name + ": " + text);
        }
        return false;
    }

    private void leaveRoom() {
        String left = room;
        if (left == null) {
            return;
        }
        room = null;
        System.out.println("[" + left + "] " + name + " đã rời phòng");
        ChatRoomServer.broadcast(left, this, "* " + name + " đã rời phòng");
    }
}
