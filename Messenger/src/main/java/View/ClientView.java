package View;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.Optional;

public class ClientView extends JFrame {

    private String name = "anon";

    private JButton nameButton;
    private JButton connect;
    private JButton send;
    private JTextArea msgTextArea;
    private JTextArea serverReply;
    private JLabel serverState;
    private JButton publicKeyButton;

    public void displayFrame() {

        Color blue = new Color(106, 143, 205);
        Dimension buttonSize = new Dimension(100, 30);

        setTitle("Blockchain Messenger");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 400));
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridLayout());

        /* Containers */
        JPanel westContainer = new JPanel(new BorderLayout());
        JPanel eastContainer = new JPanel(new BorderLayout());

        /* Components */

        /* westContainer components */
        JLabel settingsLabel = new JLabel("Settings");

        /* name setting panel */
        JPanel nameSettingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            nameSettingPanel.setBackground(new Color(0x872929));
        nameSettingPanel.setSize(new Dimension(100, 60));

        JLabel nameLabel = new JLabel("Name:");
//        nameLabel.setPreferredSize(buttonSize);

        nameButton = new JButton(name);
//        nameButton.setBackground(blue);

        nameSettingPanel.add(nameLabel);
        nameSettingPanel.add(nameButton);

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

        publicKeyButton = new JButton("...");

        publicKeyPanel.add(publicKeyLabel);
        publicKeyPanel.add(publicKeyButton);

        JPanel westGridPanel = new JPanel(new GridLayout(5,1));
        westGridPanel.add(nameSettingPanel);
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
        centerContainer.setLayout(new BoxLayout(centerContainer ,BoxLayout.Y_AXIS));

        centerContainer.add(msgScrollPane);
        centerContainer.add(serverMsg);
        centerContainer.add(serverReplyScrollPane);

        connect = new JButton("Connect");
        connect.setPreferredSize(buttonSize);

        send = new JButton("Send");
        send.setPreferredSize(buttonSize);

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
        add(westContainer);
        add(eastContainer);

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

    public JButton getPublicKeyButton() { return publicKeyButton; }

    public JTextArea getMsgTextArea() {
        return msgTextArea;
    }

    public void setServerResponse(String response) {
        response = LocalTime.now().toString().substring(0, 8) + ": " + response;
        this.serverReply.setText(serverReply.getText().length() > 0 ?
                serverReply.getText() + "\n" + response : response); /* ternary prevents line break on 1st msg */
    }

    public String getNameButtonText() { return this.nameButton.getText();}

    public void setNameButtonText(String name) {
        this.nameButton.setText(name);
    }

    public void setConnectionStatus(boolean isConnected) {
        if (isConnected) {
            serverState.setForeground(Color.GREEN);
            serverState.setText("Open");
            return;
        }
        serverState.setForeground(Color.red);
        serverState.setText("Closed");
    }
}
