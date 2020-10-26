package io.github.siaust.Model;

import io.github.siaust.Model.VirtualCoin.Transaction;
import io.github.siaust.Utils.StringUtil;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Block implements Serializable {

    private static final long serialVersionUID = -7337081154260328260L;

    private final int id;
    private final String hash;
    private final String prevBlockHash;
    private final long timestamp;
    private final String minerID;

    private float generationTime;
    private String nAdjustmentString;

    private final int zeroPrefix;
    private int magicNumber;

    private final List<Transaction> transactionList = new ArrayList<>(); // Ref to our transactions from client inc. msgID
    private final String messages;

    public Block(int id, String prevBlockHash, int zeroPrefix, String minerID) throws InterruptedException {
        this.zeroPrefix = zeroPrefix;
        timestamp = new Date().getTime();
        this.id = id;
        this.prevBlockHash = prevBlockHash;
        hash = generateHash();
        this.minerID = minerID;
        messages = getTransactions();

        transactionList.add(new Transaction(Blockchain.accounts.getOrCreateAccount("Blockchain")
                , Blockchain.accounts.getOrCreateAccount(minerID)
                , 100
                , Blockchain.getMessageID()));
    }

    private String generateHash() throws InterruptedException {
        Thread.sleep(1); // fixme why? To throw Thread exception?
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

        generationTime = (float) (LocalTime.now().toNanoOfDay() - startTime.toNanoOfDay()) / 1000000000;

        if (generationTime < 1) { // fixme get this method from Blockchain somehow. zeroPrefix set to negative?
            nAdjustmentString = "\nN was increased to " + (zeroPrefix + 1);
        } else if (generationTime > 5) {
            nAdjustmentString = "\nN was decreased to " + (zeroPrefix - 1);
        } else {
            nAdjustmentString = "\nN stays the same";
        }
        return hash;
    }

    private String getTransactions() {
        if (Blockchain.getTransactionQueue().isEmpty()) {
            return "No transactions";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = Blockchain.getTransactionQueue().size(); i > 0; i--) {
            Transaction transaction = Blockchain.getTransactionQueue().poll();
            transactionList.add(transaction);

            sb.append(String.format("\n%s", transaction));
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

    /** @return The generation time to 10 decimal places (1/billionth of a sec) */
    public float getGenerationTime() {
        return generationTime;
    }

    public List<Transaction> getTransactionList() { return transactionList; }

    @Override
    public String toString() {
        return "Block:"
                + "\nCreated by: " + minerID
                + "\n" + minerID + " gets 100 VC"
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
