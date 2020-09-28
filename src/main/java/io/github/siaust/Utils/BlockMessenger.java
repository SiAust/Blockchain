package io.github.siaust.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;

public class BlockMessenger extends Thread {

    private final Queue<String> messagesList;
    boolean running = true;

    public BlockMessenger(Queue<String> messagesList) {
        super("Block-Messenger");
        this.messagesList = messagesList;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter message: ");
            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    running = false;
                    throw new InterruptedException();
                }
                if (bufferedReader.ready()) {
                    String message = bufferedReader.readLine();
                    messagesList.add(message);
                    System.out.printf("Your message \"%s\" has been added to the queue.\n", message);
                    System.out.println("Enter message: ");
                }
            }
        } catch (InterruptedException | IOException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }
    }
}
