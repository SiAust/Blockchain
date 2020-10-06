package Utils;

import model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** Connects to a server that is generating blocks for a blockchain. The client
 * sends messages which will be inserted into the blocks as they are generated.
 * */
public class Client extends Thread implements Observable {

    private final String host;
    private final int port;
    private final Stack<Message> messages = new Stack<>();

    private final List<Observer> observers = new ArrayList<>();

    // todo: allow host/port params
    public Client(int port) {
        super("ClientServer-Thread-" + port);
        this.host = "localhost";
        this.port = port;
    }

    @Override
    public void run() {

        try (
                Socket echoSocket = new Socket(host, port);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                PrintWriter outStd = new PrintWriter(System.out);
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))
//                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            if (port == 8080) {
                notifyConnectionStatus(true);
                String serverResponse;
                while (true) {
                    if (in.ready()) {

                        serverResponse = in.readLine();
                        if (serverResponse.equals("-1")) {
                            System.out.println("Server connection closed");
                            notifyServerResponse("Server connection closed");
                            notifyConnectionStatus(false);
                            break;
                        }
                        notifyServerResponse(serverResponse);
                        System.out.println("Server received message: " + serverResponse);
                    }
                    if (!messages.isEmpty()) {
                        Message message = messages.pop();
                        out.println(message);
    //                    out.write(String.valueOf(message));

                        outStd.println(message);
                        System.out.println("PrinterWriter message: " + message);// so I can see what PrintWriter is doing?
                    }
                }
            }
            if (port == 6666) {
                String serverResponse;
                while (true) {
                    out.println("publicKey");
                    if (in.ready()) {
                        serverResponse = in.readLine();
//                        if (serverResponse.equals("-1")) { // todo: validate key and stop server
//                            System.out.println("Key request server connection closed");
//                            notifyServerResponse("Key request server connection closed");
//                            break;
//                        }
                        System.out.println("Key server message: " + serverResponse);
                        // if key valid ? "success" : "failure"
                        notifyKeyResponse(serverResponse);
                        notifyServerResponse("Key request server connection closed");
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
//            System.exit(1);
        } catch (IOException e) {
            System.out.println("Couldn't get I/O for the connection to " + host);
            notifyServerResponse("Couldn't get I/O for the connection to " + host);
            notifyConnectionStatus(false);
//            System.exit(1);
        }
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyServerResponse(String string) {
        observers.forEach(observer -> observer.responseNotification(string));
    }

    @Override
    public void notifyConnectionStatus(boolean bool) {
        observers.forEach(observer -> observer.connectionNotification(bool));
    }

    @Override
    public void notifyKeyResponse(String key) {
        observers.forEach(observer -> observer.keyNotification(key));
    }
}