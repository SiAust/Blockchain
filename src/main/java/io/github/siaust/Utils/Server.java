package io.github.siaust.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Queue;

public class Server extends Thread {

    private final Queue<String> messagesList;
    boolean running = true;
    private final int port;

    private List<byte[]> list;
    private byte[] publicKey;

    /** This constructor is for creating a runnable instance ServerSocket which listens for
     * messages from the client. It verifies the messages using the public key received from the client.
     * @param messagesList a reference to the messagesList object in the Controller class. We add messages
     * here and the controller will pass to Block when called */
    public Server(Queue<String> messagesList) {
        super("Block-Messenger");
        this.messagesList = messagesList;
        this.port = 8080;
    }

    @Override
    public void run() {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            // fixme: the below code isn't called until a connection is made, hence
            // fixme: interrupt() will not be checked and the thread will not be terminated.
            String inputLine;
            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    running = false;
                    out.println("-1"); // todo: check
                    throw new InterruptedException(Thread.currentThread().getName() + ": Server thread terminated");
                }
                int readInt = objectIn.readInt();
                /* The client sends a int to identify the forthcoming object to be received. If
                 * the int has value of zero, we will next receive the public key object */
                if (readInt == 0) {
                    if ((publicKey = (byte[]) objectIn.readObject()) != null) {
                        out.println("1");
                        KeyUtils.writeToFile(publicKey);
                    }
                }
                /* If the value of readInt is one then we are receiving a list object next */
                if (readInt == 1) {
                    if ((list = (List<byte[]>) objectIn.readObject()) != null) {
                        if (KeyUtils.verifySignature(list)) {
                            inputLine = new String(list.get(0));
                            messagesList.add(inputLine);
                            out.println("Message received: " + inputLine);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
