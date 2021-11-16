package multiChat.server;

import java.util.ArrayList;
import java.util.List;

public class ArrayQueue {
    private final int capacity = 128;
    private int size;
    private final List<String> messages = new ArrayList<>(capacity);

    public int getSize() {
        return size;
    }

    public List<String> getMessages() {
        return messages;
    }

    public synchronized void addMsg(String msg) throws InterruptedException {
        while (getSize() == capacity) {
            wait();
        }
        messages.add(msg);
    }

    public String getMsg() throws InterruptedException {
        while (getSize() == 0) {
            wait();
        }
        String msg = messages.get(0);
        messages.remove(messages.get(0));
        return msg;
    }


}
