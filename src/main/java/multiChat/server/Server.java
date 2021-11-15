package multiChat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class Server {
    private volatile List<Session> sessions = new ArrayList<>();
    private final LinkedQueue<String> messages = new LinkedQueue<>();


    public static void main(String[] args) throws IOException {
        new Server().openChannel();
    }
    public void openChannel() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    Session session = new Session(socket, messages, sessions);
                    sessions.add(session);
                    session.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
