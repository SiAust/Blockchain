package Controller;

import Utils.Observer;
import View.ClientView;
import Utils.ClientServer;
import model.Message;

import javax.swing.*;

public class Controller implements Observer {

    private final ClientView view;
    private final Message model;
    private ClientServer clientServer;

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
            int option = JOptionPane.showConfirmDialog(view,
                    "Load public key from server or file?",
                    "Public Key",
                    JOptionPane.YES_NO_CANCEL_OPTION, // fixme change dialog options?
                    JOptionPane.INFORMATION_MESSAGE);
            System.out.println("OPTION: " + option);
        });
    }

    private void sendMessage() {
        model.setName(view.getNameButtonText());
        model.setPublicKey("example key"); // fixme store a public key somewhere?
        model.setMsgContent(view.getMsgTextArea().getText().trim());
        view.getMsgTextArea().setText(""); // clear the text after sending
        clientServer.addMessage(model);

    }

    private void startConnection() {
        clientServer = new ClientServer();
        clientServer.start();
        clientServer.addObserver(this);
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
}
