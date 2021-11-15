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

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.setHost("127.0.0.1");
        client.setPort(3000);
        client.start();
    }

    private void start() throws IOException {
        try(Socket socket = new Socket(host, port);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));) {
            MsgReceiver msgReceiver = new MsgReceiver(bufferedReader);
            new Thread(msgReceiver).start();
            MsgSender msgSender = new MsgSender(bufferedWriter);
            new Thread(msgSender).start();
        }
    }

    private class MsgReceiver extends Thread {
        BufferedReader bufferedReader;

        MsgReceiver(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public void run() {
            try {
                while (!stopped) {
                    String str = getServerMsg(bufferedReader);
                    System.out.println(str);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to read message from server. " + e);
            }
        }

        private String getServerMsg(BufferedReader bufferedReader) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
                String line= "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
        }
    }

    private class MsgSender extends Thread {
        BufferedWriter bufferedWriter;

        MsgSender(BufferedWriter bufferedWriter) {
            this.bufferedWriter = bufferedWriter;
        }

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
            bufferedWriter.flush();
        }

        private String getConsoleInput() {
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
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

}
