package multiChat.client1;

import java.io.*;
import java.net.Socket;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class Client {
    private boolean stopped;
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;


    public Client(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            this.socket = new Socket(host, port);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new MsgSender().start();
            new MsgReceiver().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("127.0.0.1", 3000);
    }

    private class MsgReceiver extends Thread {

        @Override
        public void run() {
            try {
                while (!stopped) {
                    String str = getServerMsg();
                    System.out.println(str);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable read message from server. " + e);
            }
        }

        private String getServerMsg() throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            if ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }

    private class MsgSender extends Thread {

        public void setStop() {
            stopped = true;
        }
        @Override
        public void run() {
            try {
                sendMessage(bufferedWriter, getConsoleInput());
            } catch (IOException e) {
            throw new RuntimeException("Unable to write client message. " + e);
            }
        }

        public void sendMessage(BufferedWriter bufferedWriter, String clientMsg) throws IOException {
            if (clientMsg == null) {
                throw new NullPointerException("You must enter a valid value to send the message.");
            }
            bufferedWriter.write(clientMsg + "\r\n");
        }

        private String getConsoleInput() {
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.println("Enter your message:");
                String input = consoleReader.readLine();
                if (input.equalsIgnoreCase("exit")) {
                    setStop();
                }
                return input;
            } catch (IOException e) {
                throw new RuntimeException("Unable to read console message" + e);
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
