package multiChat.client2;

import java.io.*;
import java.net.Socket;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.start();
    }

    public void start() throws IOException {

        try(Socket socket = new Socket("127.0.0.1", 3000);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));) {
            String inputMsg = getInputMsg();
            sendMessage(bufferedWriter, inputMsg);
            String answer = getServerMsg(bufferedReader);
            System.out.println(answer);
        }
    }

    public void sendMessage(BufferedWriter bufferedWriter, String inputMsg) throws IOException {
        if (inputMsg == null) {
            throw new NullPointerException("You must enter a valid value to send the message.");
        }
        try {
            bufferedWriter.write(inputMsg + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IOException("Unable to write client message. " + e);
        }
    }

    private String getServerMsg(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line= "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new IOException("Unable to read echo message from server. " + e);
        }
    }

    private String getInputMsg() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new IOException("Unable to read console message" + e);
        }
    }
}
