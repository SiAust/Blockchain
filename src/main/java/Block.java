import java.util.Date;

public class Block {

    private final int id;
    private final String hash;
    private final String prevBlockHash;
    private final long timestamp;

    public Block(int id, String prevBlockHash) {
        timestamp = new Date().getTime();
        this.id = id;
        this.prevBlockHash = prevBlockHash;
        hash = generateHash();
    }

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private String generateHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(timestamp).append(prevBlockHash);

//        System.out.println("String to be hashed: " + sb.toString());

        return StringUtil.applySha256(sb.toString());
    }

    @Override
    public String toString() {

        return "Block:\n"
                + "Id: " + id
                + "\nTimestamp: " + timestamp
                + "\nHash of the previous block:\n" + prevBlockHash // todo: placeholder
                + "\nHash of the block:\n" + hash;
    }
}
