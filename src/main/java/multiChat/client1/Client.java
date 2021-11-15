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


    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        try {
            this.socket = new Socket(host, port);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        start(socket);
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 3000);
        client.getConsoleInput();
    }

    private void getConsoleInput() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter your message:");
            String input = null;
            while (true) {
                input = consoleReader.readLine();
                if (input.equalsIgnoreCase("exit")) {
                    stopped = true;
                }
                sendMessage(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read console message" + e);
        }
    }

    public void sendMessage(String clientMsg) throws IOException {
        if (clientMsg == null) {
            throw new NullPointerException("You must enter a valid value to send the message.");
        }
        bufferedWriter.write(clientMsg + "\r\n");
        bufferedWriter.flush();
    }

    private void start(Socket socket) throws IOException {
        //new MsgSender(bufferedWriter).start();
        new MsgReceiver(bufferedReader, socket).start();
    }

    private class MsgReceiver extends Thread {
        private final BufferedReader bufferedReader;
        private Socket socket;

        public MsgReceiver(BufferedReader bufferedReader, Socket socket) {
            this.bufferedReader = bufferedReader;
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader thisReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!stopped) {
                    String str = getServerMsg(thisReader);
                    if (str.equals("")) {
                        continue;
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable read message from server. " + e);
            }
        }

        private synchronized String getServerMsg(BufferedReader bufferedReader) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            if (bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine());
            }
            return stringBuilder.toString();
        }
    }

    /*private class MsgSender extends Thread {
        private final BufferedWriter bufferedWriter;

        public MsgSender(BufferedWriter bufferedWriter) {
            this.bufferedWriter = bufferedWriter;
        }

        public void setStop() {
            stopped = true;
        }
        @Override
        public void run() {
            *//*try {
                sendMessage(bufferedWriter, getConsoleInput());
            } catch (IOException e) {
            throw new RuntimeException("Unable to write client message. " + e);
            }*//*
        }

        *//*public void sendMessage(BufferedWriter bufferedWriter, String clientMsg) throws IOException {
            if (clientMsg == null) {
                throw new NullPointerException("You must enter a valid value to send the message.");
            }
            bufferedWriter.write(clientMsg + "\r\n");
            bufferedWriter.flush();
        }*//*

        *//*private String getConsoleInput() {
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
        }*//*
    }*/

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
