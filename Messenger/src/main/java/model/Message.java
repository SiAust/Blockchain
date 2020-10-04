package model;

import java.time.LocalDateTime;

public class Message {

    private String name;
    private String msgContent;
    private String publicKey;
    private String timeStamp;

    public Message() {
        this("anon", "key");
    }

    public Message(String name, String publicKey) {
        this.name = name;
        this.publicKey = publicKey;
        this.timeStamp = LocalDateTime.now().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return name + ": " + msgContent;

    }
}
