package Controller;

import Utils.Client;
import Utils.KeyUtils;
import Utils.Observer;
import View.ClientView;
import model.TransactionRequest;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Controller implements Observer {

    private final ClientView view;
    private TransactionRequest model;
    private Client serverMessenger;

    private String host;
    private int port;

    private int messageID;

    private int coins = 1;

    public Controller(TransactionRequest model, ClientView view) {
        this.model = model;
        this.view = view;
    }

    public void init() {
        host = "localhost";
        port = 8080;

        view.displayFrame();
        view.getConnect().addActionListener(e -> {
            System.out.println("[" + Controller.class.getSimpleName() + "] View connect button pressed");
            startConnection();
        });
        view.getHostButton().addActionListener(e -> {
            String host = JOptionPane.showInputDialog("Input host address");
            this.host = host;
            setButtonText(view.getHostButton(), host);
        });
        view.getPortButton().addActionListener(e -> {
            int port;
            try { // fixme: cancel button returns ? null? throws exception, bit weird
                port = Integer.parseInt(JOptionPane.showInputDialog("Input port number"));
                this.port = port;
                view.getPortButton().setText(String.valueOf(port));
            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(view.getPortButton(), "Invalid port number!");
            } catch (HeadlessException headlessException) {
                headlessException.printStackTrace();
            }
        });
        view.getNameButton().addActionListener(e -> {
            System.out.println("[" + Controller.class.getSimpleName() + "] View name button pressed");
            String name = JOptionPane.showInputDialog("Input your name");
            setButtonText(view.getNameButton(), name);
        });
        view.getSend().addActionListener(e -> {
            System.out.println("[" + Controller.class.getSimpleName() + "] Send button pressed");
            sendMessage(); // fixme: only send message when valid recipient selected
        });
        view.getCoinSpinner().addChangeListener(e -> {
            coins = Integer.parseInt(view.getCoinSpinner().getValue().toString());
            System.out.println("[" + Controller.class.getSimpleName() + "] coinSpinner: " + view.getCoinSpinner().getValue().toString());
        });
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

    private void setAccountsList(String JSON) {
        view.setListModel(JSON);
    }

    private void startConnection() {
        serverMessenger = new Client(host, port);
        serverMessenger.start();
        serverMessenger.addObserver(this);
    }

    private void sendMessage() { // fixme: lag only on first click
        model = new TransactionRequest(view.getNameButtonText() // from account name
                , view.getRecipient() // to account name
                , coins // amount to send
                , messageID);
        serverMessenger.addMessage(model);
    }

    private void setButtonText(JButton button, String s) {
        if (s != null && s.length() > 0 ) {
            button.setText(s);
        }
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

    @Override
    public void updateAccountsJSON(String JSON) {
        setAccountsList(JSON);
    }
}
