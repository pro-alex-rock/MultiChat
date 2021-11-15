package multiChat.server;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class LinkedQueue<T> {
    private final int capacity = 10;
    private Node head;
    private int size;

    public synchronized void enqueue(T value) throws InterruptedException {
        while (size == capacity) {
            wait();
        }
        Node newNode = new Node(value);
        if (size == 0) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }

        size++;
        notify();
    }

    public synchronized T dequeue() throws InterruptedException {
        while (size == 0) {
            wait();
        }
        T result = head.value;
        head = head.next;
        size--;

        notify();
        return result;
    }


    private class Node {
        private T value;
        private Node next;

        public Node(T value) {
            this.value = value;
        }
    }
}
