package io.github.siaust.Model;

public class Message {

    private final String name;
    private final String msgContent;
    private final int messageID;

    public Message(String name, String msgContent, int messageID) {
        this.name = name;
        this.msgContent = msgContent;
        this.messageID = messageID;
    }

    public String getName() {
        return name;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public int getMessageID() {
        return messageID;
    }
}
