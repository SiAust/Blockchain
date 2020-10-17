package View;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class ClientView extends JFrame {

    private JButton nameButton;
    private JButton connect;
    private JButton send;

    private JTextArea msgTextArea;
    private JTextArea serverReply;

    private JLabel serverState;
    private JLabel publicKeyStatus;

    public void displayFrame() {

        Color blue = new Color(106, 143, 205);
        Dimension buttonSize = new Dimension(100, 30);

        setTitle("Blockchain Messenger");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 400));
        setResizable(false);
        setLocationRelativeTo(null);
        /* Top level container so we can have some padding between the JFrame */
        JPanel container = new JPanel(new GridLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        connect = new JButton("Connect");
        connect.setPreferredSize(buttonSize);

        send = new JButton("Send");
        send.setPreferredSize(buttonSize);

        /* Containers */
        JPanel westContainer = new JPanel(new BorderLayout());
        JPanel eastContainer = new JPanel(new BorderLayout());
        eastContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        /* Components */

        /* westContainer components */
        JLabel settingsLabel = new JLabel("Settings");

        /* name setting panel */
        JPanel nameSettingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameSettingPanel.setSize(new Dimension(100, 60));

        JLabel nameLabel = new JLabel("Name:");
        nameButton = new JButton("Simon Aust");

        nameSettingPanel.add(nameLabel);
        nameSettingPanel.add(nameButton);
        
        /* host name panel */
        JPanel hostNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hostNamePanel.setSize(new Dimension(100, 60));

        JLabel hostLabel = new JLabel("Host:");
        JButton hostButton = new JButton("192.168.1.1");

        hostNamePanel.add(hostLabel);
        hostNamePanel.add(hostButton);
        
        /* host port panel */
        JPanel hostPortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hostPortPanel.setSize(new Dimension(100, 60));

        JLabel portLabel = new JLabel("Port:");
        JButton portButton = new JButton("8080");

        hostPortPanel.add(portLabel);
        hostPortPanel.add(portButton);

        /* server status panel */
        JPanel serverStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        serverStatus.setBackground(new Color(210, 19, 19));
        serverStatusPanel.setSize(new Dimension(100, 60));

        JLabel serverLabel = new JLabel("Connection status:");
        serverState = new JLabel();
        setConnectionStatus(false);

        serverStatusPanel.add(serverLabel);
        serverStatusPanel.add(serverState);

        /* Public key panel */
        JPanel publicKeyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel publicKeyLabel = new JLabel("Public Key:");

        publicKeyStatus = new JLabel();
        setPublicKeyStatus(false);

        publicKeyPanel.add(publicKeyLabel);
        publicKeyPanel.add(this.publicKeyStatus);

        JPanel westGridPanel = new JPanel(new GridLayout(6, 1));
        westGridPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        westGridPanel.add(nameSettingPanel);
        westGridPanel.add(hostNamePanel);
        westGridPanel.add(hostPortPanel);
        westGridPanel.add(publicKeyPanel);
        westGridPanel.add(serverStatusPanel);


        /* eastContainer components */
        JLabel instructions = new JLabel("Input text message below:");

        msgTextArea = new JTextArea();
        msgTextArea.setLineWrap(true);
//        msgTextArea.setPreferredSize(new Dimension(200, 50)); // fixme doesn't alter anything
//        msgTextArea.setBackground(new Color(0x577EAA));

        JScrollPane msgScrollPane = new JScrollPane(msgTextArea);
        msgScrollPane.createHorizontalScrollBar();
        msgScrollPane.setPreferredSize(new Dimension(300, 50));

        JLabel serverMsg = new JLabel("Server response: ");

        serverReply = new JTextArea();
        serverReply.setLineWrap(true);
        serverReply.setEnabled(false);

        JScrollPane serverReplyScrollPane = new JScrollPane(serverReply);
        serverReplyScrollPane.setPreferredSize(new Dimension(300, 50));

        /* Seems to stop serverMsg label floating right when text entered into msgTextArea */
        msgScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        serverMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        serverReplyScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        centerContainer.add(msgScrollPane);
        centerContainer.add(serverMsg);
        centerContainer.add(serverReplyScrollPane);

        JPanel eastBtnPanel = new JPanel(new FlowLayout());
        eastBtnPanel.add(connect);
        eastBtnPanel.add(send);

        /* Adding components to panels */

        /* west */
        westContainer.add(settingsLabel, BorderLayout.NORTH);
        westContainer.add(westGridPanel);

        /* east */
        eastContainer.add(instructions, BorderLayout.NORTH);
        eastContainer.add(centerContainer, BorderLayout.CENTER);
        eastContainer.add(eastBtnPanel, BorderLayout.SOUTH);

        /* frame */
        container.add(westContainer);
        container.add(eastContainer);

        add(container);
        setVisible(true);
        pack();
    }


    public JButton getNameButton() {
        return nameButton;
    }

    public JButton getConnect() {
        return connect;
    }

    public JButton getSend() {
        return send;
    }

    public JLabel getPublicKeyStatus() {
        return publicKeyStatus;
    }

    public JTextArea getMsgTextArea() {
        return msgTextArea;
    }

    public void setServerResponse(String response) {
        response = LocalTime.now().toString().substring(0, 8) + ": " + response;
        this.serverReply.setText(serverReply.getText().length() > 0 ?
                serverReply.getText() + "\n" + response : response); /* ternary prevents line break on 1st msg */
    }

    public String getNameButtonText() {
        return this.nameButton.getText();
    }

    public void setNameButtonText(String name) {
        this.nameButton.setText(name);
    }

    public void setConnectionStatus(boolean isConnected) {
        if (isConnected) {
            serverState.setForeground(Color.GREEN);
            serverState.setText("Open");
            connect.setEnabled(false);
            send.setEnabled(true);
            return;
        }
        serverState.setForeground(Color.red);
        serverState.setText("Closed");
        connect.setEnabled(true);
        send.setEnabled(false);
    }

    public void setPublicKeyStatus(boolean isShared) {
        if (isShared) {
            publicKeyStatus.setText("Sent, received");
            publicKeyStatus.setForeground(Color.GREEN);
            return;
        }
        publicKeyStatus.setText("Not shared");
        publicKeyStatus.setForeground(Color.RED);
    }
}
