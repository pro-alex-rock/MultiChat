package multiChat.server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class Session extends Thread {
    private final Socket socket;
    private final LinkedQueue<String> messages;
    private List<Session> sessions;

    public Session(Socket socketClient, LinkedQueue<String> messages, List<Session> sessions) {
        this.socket = socketClient;
        this.messages = messages;
        this.sessions = sessions;
    }

    public void run() {
        connect();
    }

    private void connect() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String msg = getClientMsg(bufferedReader);
                if(msg.equalsIgnoreCase("exit")) {
                    break;
                }
                messages.enqueue(msg);
                sendToAllRoom(bufferedWriter, messages.dequeue());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendToAllRoom(BufferedWriter bufferedWriter, String msg) {
        for (Session session : sessions) {
            /*if (this.equals(session)) {
                continue;
            }*/
            session.sendMsg(bufferedWriter, msg);
        }
    }

    private void sendMsg(BufferedWriter bufferedWriter, String msg) {
        try {
            bufferedWriter.write(msg + "\r\n");
        } catch (IOException e) {
            throw new RuntimeException("Unable write message to clients. " + e);
        }
    }

    private String getClientMsg(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        Session session = (Session) o;
        return socket.getPort() == session.socket.getPort();
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }
}
