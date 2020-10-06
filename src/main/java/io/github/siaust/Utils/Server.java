package io.github.siaust.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class Server extends Thread {

    private final Queue<String> messagesList;
    boolean running = true;
    private final int port;

    public Server(Queue<String> messagesList) {
        super("Block-Messenger");
        this.messagesList = messagesList;
        this.port = 8080;
    }

    public Server(int port) {
        super("Key-Listener");
        this.port = port;
        this.messagesList = null;
    }


    @Override
    public void run() {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            // fixme: the below code isn't called until a connection is made, hence
            // fixme: interrupt() will not be checked and the thread will not be terminated.
            String inputLine;
            if (port == 8080) {
                while (running) {
                    if (Thread.currentThread().isInterrupted()) {
                        running = false;
                        out.println(-1);
                        throw new InterruptedException(Thread.currentThread().getName() + ": Server thread terminated");
                    }
                    while (in.ready()) {
                        if ((inputLine = in.readLine()) != null) {
                            out.println(inputLine);
                            // if inputLine == "give me public key" / out.println(publicKey.txt) ?
                            messagesList.add(inputLine);
                        }
                    }
                }
            }
            /* We handle listening for a client request for the public key */
            if (port == 6666) {
                while (running) {
                    if (Thread.currentThread().isInterrupted()) {
                        running = false;
                        out.println(-1);
                        throw new InterruptedException(Thread.currentThread().getName() + ": Server thread terminated");
                    }
                    while (in.ready()) {
                        if ((inputLine = in.readLine()) != null) {
                            // if inputLine == "give me public key" / out.println(publicKey.txt) ?
                            if (inputLine.equals("publicKey")) {
                                out.println("here is your public key! =KEYKEYKEY= ");
                            }
                            out.println(inputLine);
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
