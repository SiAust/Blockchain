package Controller;

import Utils.Client;
import Utils.Keys;
import Utils.Observer;
import View.ClientView;
import model.Message;

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
    private Message model;
    private Client serverMessenger;

    public Controller(Message model, ClientView view) {
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
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("KeyTyped: " + e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    if (view.getSend().isEnabled()) {
                        sendMessage();
                        view.getMsgTextArea().setCaretPosition(0);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        generateKeys();
        startConnection();
    }

    /* Creates the public and private keys needed to sign the message before sending to server */
    private void generateKeys() {
        /* Prevent unnecessary creation of keys */ // todo: make sure keys valid/regenerate keys
        if (Files.exists(Path.of(Keys.publicKeyPath)) && Files.exists(Path.of(Keys.privateKeyPath))) {
            return;
        }
        Keys gk;
        try {
            gk = new Keys(1024);
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
        model = new Message(view.getNameButtonText(), view.getMsgTextArea().getText().trim(), Keys.privateKeyPath);
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
}
