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

    public Session(Socket socketClient) {
        this.socket = socketClient;
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
                Server.messages.addMsg(msg);
                sendToAllRoom(bufferedWriter, Server.messages.getMsg());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendToAllRoom(BufferedWriter bufferedWriter, String msg) {
        while (Server.sessions.size() > 0) {
        for (Session session : Server.sessions) {
            /*if (this.equals(session)) {
                continue;
            }*/
            session.sendMsg(bufferedWriter, msg);
        }
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
