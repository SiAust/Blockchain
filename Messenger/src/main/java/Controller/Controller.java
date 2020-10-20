package Controller;

import Utils.Client;
import Utils.KeyUtils;
import Utils.Observer;
import View.ClientView;
import model.TransactionRequest;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Controller implements Observer {

    private final ClientView view;
    private TransactionRequest model;
    private Client serverMessenger;

    private int messageID;

    public Controller(TransactionRequest model, ClientView view) {
        this.model = model;
        this.view = view;
    }

    public void init() {
        view.displayFrame();
        view.getConnect().addActionListener(e -> {
            System.out.println("View Connect button pressed");
        });
        view.getNameButton().addActionListener(e -> {
            System.out.println("View Name button pressed");
            String name = JOptionPane.showInputDialog("Input your name");
            view.setNameButtonText(name != null && name.length() > 0 ? name :
                    view.getNameButtonText().length() > 0 ? view.getNameButtonText() : "anon");
        });
        view.getSend().addActionListener(e -> {
            System.out.println("View Send button pressed");
            sendMessage();
        });
        view.getConnect().addActionListener(e -> {
            startConnection();
        });
        view.getMsgTextArea().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    if (view.getSend().isEnabled()) {
                        sendMessage();
                        view.getMsgTextArea().setCaretPosition(0);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        // todo: implement listeners for host name and port buttons
        // todo: serialize so we can keep settings
        generateKeys();
        startConnection();
    }

    /* Creates the public and private keys needed to sign the message before sending to server */
    private void generateKeys() {
        /* Prevent unnecessary creation of keys */ // todo: make sure keys valid/regenerate keys
        if (Files.exists(Path.of(KeyUtils.publicKeyPath)) && Files.exists(Path.of(KeyUtils.privateKeyPath))) {
            return;
        }
        KeyUtils gk;
        try {
            gk = new KeyUtils(1024);
            gk.createKeys();
            gk.writeToFile(true, gk.getPublicKey().getEncoded());
            gk.writeToFile(false, gk.getPrivateKey().getEncoded());
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void startConnection() {
        serverMessenger = new Client(8080);
        serverMessenger.start();
        serverMessenger.addObserver(this);
    }

    private void sendMessage() {
        model = new TransactionRequest(view.getNameButtonText() // from account name
                , view.getMsgTextArea().getText().trim() // to account name
                , 100L // amount to send
                , messageID);
        view.getMsgTextArea().setText(""); // clear the text after sending
        serverMessenger.addMessage(model);
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
        if (isConnected) {
            setServerResponse("Connected to server");
        }
    }

    @Override
    public void keyNotification(boolean hasKey) {
        if (hasKey) {
            view.setPublicKeyStatus(true);
            setServerResponse("Public key received");
            return;
        }
        view.setPublicKeyStatus(false);
    }

    @Override
    public void updatedMsgID(int id) {
        this.messageID = id;
    }
}
