package io.github.siaust.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

        int portNumber = 8080;

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    running = false;
                    out.println(-1);
                    throw new InterruptedException(Thread.currentThread().getName() + ": Server thread terminated");
                }
                if (in.ready()) {
                    if ((inputLine = in.readLine()) != null) {
                        out.println(inputLine);
                        messagesList.add(inputLine);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
//            System.out.println(e.getMessage());
        }
    }
}
