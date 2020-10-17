package Utils;

import model.Message;

import java.io.*;
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
                ObjectOutputStream objectOut = new ObjectOutputStream(echoSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))
        ) {
            notifyConnectionStatus(true);
            objectOut.writeInt(1); // so Server ObjectInputStream knows we're sending the publicKey object
            objectOut.writeObject(KeyUtils.getPublic().getEncoded());
            String serverResponse;
            while (true) {
                if (in.ready()) {
                    serverResponse = in.readLine();
                    if (serverResponse.equals("-1")) {
                        notifyConnectionStatus(false);
                        notifyKeyResponse(false);
                        notifyServerResponse("Server connection closed");
                        break;
                    } else if (serverResponse.equals("1")) {
                        notifyKeyResponse(true);
                        serverResponse = in.readLine();
                        notifyMsgID(Integer.parseInt(serverResponse));
                        System.err.println("This is the first messageID: " + serverResponse); // todo: we receive msgID for first time
                    } else {
                        notifyServerResponse(serverResponse);
                        System.out.println("Server received message: " + serverResponse);
                    }
                }
                if (!messages.isEmpty()) {
                    objectOut.writeInt(2); // so ObjectInputStream knows we're sending the list object
                    Message message = messages.pop();
                    objectOut.writeObject(message.getList());
                    serverResponse = in.readLine(); // todo: each time we send a msg we receive a new msgID
                    notifyMsgID(Integer.parseInt(serverResponse));
                    System.err.println("This is the next messageID: " + serverResponse);
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
        } catch (Exception e) {
            e.printStackTrace();
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
    public void notifyKeyResponse(boolean hasKey) {
        observers.forEach(observer -> observer.keyNotification(hasKey));
    }

    @Override
    public void notifyMsgID(int id) {
        observers.forEach(observer -> observer.updatedMsgID(id));
    }
}