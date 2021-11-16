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
    static volatile List<Session> sessions = new ArrayList<>();
    volatile static ArrayQueue messages = new ArrayQueue();


    public static void main(String[] args) throws IOException {
        new Server().openChannel();
    }
    public void openChannel() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    Session session = new Session(socket);
                    sessions.add(session);
                    session.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
