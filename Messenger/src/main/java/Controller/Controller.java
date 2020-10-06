package Controller;

import Utils.Observer;
import View.ClientView;
import Utils.Client;
import model.Message;

import javax.swing.*;

public class Controller implements Observer {

    private final ClientView view;
    private final Message model;
    private Client serverMessenger;
    private Client serverKeyRequest;

    public Controller(Message model, ClientView view) {
        this.model = model;
        this.view = view;
        view.displayFrame();
        startConnection();
    }

    public void init() {
        view.getConnect().addActionListener(e -> {
            System.out.println("View Connect button pressed");
        });
        view.getNameButton().addActionListener(e -> {
            System.out.println("View Name button pressed");
            String name = JOptionPane.showInputDialog("Input your name");
            view.setNameButtonText(name);
        });
        view.getSend().addActionListener(e -> {
            System.out.println("View Send button pressed");
            sendMessage();
        });
        view.getConnect().addActionListener(e -> {
            startConnection();
        });
        view.getPublicKeyButton().addActionListener(e -> {
            int option = JOptionPane.showOptionDialog(view,
                    "Load public key from server or file?",
                    "Get Public Key",
                    0,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"File", "Server", "Cancel"},
                    null);
            if (option == 0) {
                System.out.println("Opening file dialog");
                int fileOption = new JFileChooser().showDialog(view, "Select"); // todo: select file
            }
            if (option == 1) {
                System.out.println("Getting key from server");
                requestPublicKey();
            }
            System.out.println("OPTION: " + option);
        });
    }

    private void startConnection() {
        serverMessenger = new Client(8080);
        serverMessenger.start();
        serverMessenger.addObserver(this);
    }

    private void sendMessage() {
        model.setName(view.getNameButtonText());
        model.setPublicKey("example key"); // fixme store a public key somewhere?
        model.setMsgContent(view.getMsgTextArea().getText().trim());
        view.getMsgTextArea().setText(""); // clear the text after sending
        serverMessenger.addMessage(model);

    }

    private void requestPublicKey() {
        serverKeyRequest = new Client(6666);
        serverKeyRequest.start();
        serverKeyRequest.addObserver(this);

    }

    private void setServerResponse(String response) {
        view.setServerResponse(response);
    }

    @Override
    public void responseNotification(String response) {
        setServerResponse(response);
    }

    @Override
    public void connectionNotification(boolean isConnected) {
        view.setConnectionStatus(isConnected);
    }

    @Override
    public void keyNotification(String key) {
        System.out.println("Key received in controller: " + key);
    }
}
