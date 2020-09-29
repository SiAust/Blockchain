package io.github.siaust.Model;

import io.github.siaust.Utils.StringUtil;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;

public class Block implements Serializable {

    private static final long serialVersionUID = -7337081154260328260L;
    private final int id;
    private final String hash;
    private final String prevBlockHash;
    private final long timestamp;
    private final String minerID;

    private int generationTime;
    private String nAdjustmentString;

    private final int zeroPrefix;
    private int magicNumber;

    private String messages;

    public Block(int id, String prevBlockHash, int zeroPrefix, String minerID) throws InterruptedException {
        this.zeroPrefix = zeroPrefix;
        timestamp = new Date().getTime();
        this.id = id;
        this.prevBlockHash = prevBlockHash;
        hash = generateHash();
        this.minerID = minerID;
        messages = formatMessages();
    }

    private String generateHash() throws InterruptedException {
        Thread.sleep(1);
        LocalTime startTime = LocalTime.now();

        Random random = new Random();
        int upperBound = 100_000_000;
        int lowerBound = 10_000_000;
        magicNumber = random.nextInt(upperBound - lowerBound) + lowerBound;

        StringBuilder sb = new StringBuilder();
        String hashString = String.valueOf(sb.append(id).append(timestamp).append(prevBlockHash));

        String hash = StringUtil.applySha256(magicNumber + hashString);
//        String pattern = String.format("^0{%d}[^0].*", zeroPrefix);
        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < zeroPrefix; i++) {
            patternBuilder.append('0');
        }
        while (!hash.startsWith(patternBuilder.toString())) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException(Thread.currentThread().getName());
            }
            magicNumber = random.nextInt(upperBound - lowerBound) + lowerBound;
            hash = StringUtil.applySha256(magicNumber + hashString);
        }

        generationTime = LocalTime.now().toSecondOfDay() - startTime.toSecondOfDay();
        if (generationTime < 15) {
            nAdjustmentString = "\nN was increased to " + (zeroPrefix + 1);
        } else if (generationTime > 60) {
            nAdjustmentString = "\nN was decreased to " + (zeroPrefix - 1);
        } else {
            nAdjustmentString = "\nN stays the same";
        }
        return hash;
    }

    private String formatMessages() {
        if (Blockchain.getMessageQueue().isEmpty()) {
            return "no messages";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = Blockchain.getMessageQueue().size(); i > 0; i--) {
            sb.append(String.format("\n%s", Blockchain.getMessageQueue().poll()));
//            Blockchain.getMessageQueue().forEach(e -> sb.append(String.format("\n%s", e)));
        }

        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public int getGenerationTime() {
        return generationTime;
    }

    @Override
    public String toString() {
        return "Block:"
                + "\nCreated by miner # " + minerID
                + "\nId: " + id
                + "\nTimestamp: " + timestamp
                + "\nMagic number: " + magicNumber
                + "\nHash of the previous block:\n" + prevBlockHash
                + "\nHash of the block:\n" + hash
                + "\nBlock data: " + messages
                + "\nBlock was generating for " + generationTime + " seconds"
                + nAdjustmentString;
    }
}
