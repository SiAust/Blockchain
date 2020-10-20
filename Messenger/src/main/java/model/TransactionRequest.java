package model;

import Utils.KeyUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionRequest {

    List<byte[]> list; // todo should this be here?

    private String from;
    private String to;
    private long coins;
    private int messageID;

    public TransactionRequest() {}

    public TransactionRequest(String from, String to, long coins, int messageID) {
        this.list = new ArrayList<>();

        this.from = from;
        this.to = to;
        this.coins = coins;
        this.messageID = messageID;

        this.list.add(this.toString().getBytes()); // our message as a byte[]
        try {
            this.list.add(KeyUtils.sign(this.toString())); // our signature
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
        return String.format("{\"from\":\"%s\"," +
                "\"to\":\"%s\"," +
                "\"coins\":%d," +
                "\"messageID\":%d}", from, to, coins, messageID);
    }
}
