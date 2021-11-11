package multiChat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class Session implements Runnable {
    private final Socket socket;
    private final List<Session> sessions;

    public Session(Socket socketClient, List<Session> sessions) {
        this.socket = socketClient;
        this.sessions = sessions;
    }

    public void run() {
        connect();
    }

    private void connect() {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            while (true) {
                String msg = getClientMsg(bufferedReader);
                if(msg.equalsIgnoreCase("exit")) {
                    break;
                }
                synchronized (sessions) {
                    for (Session session : sessions) {
                        if (this.equals(session)) {
                            continue;
                        }
                        session.sendMsg(bufferedWriter, msg);
                    }
                }
            }

        } catch (IOException e) {}
    }

    private void sendMsg(BufferedWriter bufferedWriter, String msg) {
        try {
            bufferedWriter.write(("echo: " + msg));
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable write message to client. " + e);
        }
    }

    private String getClientMsg(BufferedReader bufferedReader) throws IOException {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new IOException("Unable read client message" + e);
        }
    }
}
