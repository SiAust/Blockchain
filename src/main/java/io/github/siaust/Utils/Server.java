package io.github.siaust.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.siaust.Model.Blockchain;
import io.github.siaust.Model.VirtualCoin.Transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private ObjectInputStream objectIn;


    private final Queue<Transaction> transactions;
    boolean running = true;
    private final int port;

    private List<byte[]> list;
    private byte[] publicKey;

    private Supplier<Integer> msgIDSupplier;

    /** This constructor is for creating a runnable instance ServerSocket which listens for
     * messages from the client. It verifies the messages using the public key received from the client.
     * @param transactions a reference to the transactions object in the Controller class. We add messages
     * here and the controller will pass to Block when called */
    public Server(Queue<Transaction> transactions, Supplier<Integer> msgIDSupplier) {
        super("Blockchain-Server");
        this.transactions = transactions;
        this.port = 8080;

        this.msgIDSupplier = msgIDSupplier;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            objectIn = new ObjectInputStream(clientSocket.getInputStream());

            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    running = false;
                    out.println("-1");
                    throw new InterruptedException(Thread.currentThread().getName() + ": Server thread terminated");
                }
                int messageType = 0;
                if (objectIn.available() >= 4) { /* prevents readInt() from blocking */
                    messageType = objectIn.readInt();
                }
                /* The client sends a int to identify the forthcoming object to be received. If
                 * the int has value of one, we will next receive the public key object */
                if (messageType == 1) {
                    if ((publicKey = (byte[]) objectIn.readObject()) != null) {
                        out.println("1");
                        KeyUtils.writeToFile(publicKey);
                        out.println(msgIDSupplier.get()); // send the generated messageID to client
                    } // fixme: sent msgID updated and serialized, but if client doesn't send a msg?
                }
                /* If the value of readInt is two then we are receiving a list object (message + signature) next */
                if (messageType == 2) {
                    if ((list = (List<byte[]>) objectIn.readObject()) != null) {
                        if (KeyUtils.verifySignature(list)) {
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObject = (JsonObject) jsonParser.parse(new String(list.get(0)));
                            Transaction transaction = new Transaction(
                                    Blockchain.accounts.getOrCreateAccount(jsonObject.get("from").getAsString()),       // account from
                                    Blockchain.accounts.getOrCreateAccount(jsonObject.get("to").getAsString()),         // account to
                                    jsonObject.get("coins").getAsLong(),        // coins
                                    jsonObject.get("messageID").getAsInt());    // messageID

                            transactions.add(transaction);
                            out.println(msgIDSupplier.get()); // send the next generated messageID to client
                            out.println("Transaction accepted: " + transaction);
                        } else {
                            out.println("Transaction rejected: signature or ID invalid");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("The server socket closed in the try/catch block");
        } catch (InterruptedException e) {
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                clientSocket.close(); // fixme: NPE if no connections made
                out.close();
                objectIn.close();
            } catch (IOException e) {
                System.out.println("The server socket closed in the finally block");
            } catch (NullPointerException e) {
                System.out.println("NPE in the Server thread");
            }
        }
    }

    /** This is to force the ServerSocket to close if being blocked by the accept() method */
    public void closeServer() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("The socket server closed in closerServer() method");
        }
    }
}
