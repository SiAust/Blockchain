package model;

import Utils.KeyUtils;

import java.util.ArrayList;
import java.util.List;

public class Message {

    List<byte[]> list; // todo should this be here?

    private String name;
    private String msgContent;
    private int messageID;

    public Message() {}

    public Message(String name, String msgContent, String keyFile, int messageID) {
        this.list = new ArrayList<>();

        this.name = name;
        this.msgContent = msgContent;
        this.messageID = messageID;

        this.list.add(this.toString().getBytes()); // our message as a byte[]
        try {
            this.list.add(KeyUtils.sign(this.toString(), keyFile)); // our signature
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** @return The list which contains our JSON message string in bytes in index 0,
     * and the signature of the message in index 1 */
    public List<byte[]> getList() {
        return list;
    }

    /** @return A String formatted in JSON syntax. This allows easy extraction of parts of data from the
     * string */
    @Override
    public String toString() {
        return String.format("{\"name\":\"%s\"," +
                "\"msgContent\":\"%s\"," +
                "\"messageID\":%d}", name, msgContent, messageID);
    }
}
