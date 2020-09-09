package io.github.siaust.Model;

import io.github.siaust.Utils.StringUtil;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;

public class Block  implements Serializable {

    private static final long serialVersionUID = -7337081154260328260L;
    private final int id;
    private final String hash;
    private final String prevBlockHash;
    private final long timestamp;

    private int generationTime;

    private int zeroPrefix;
    private int magicNumber;

    public Block(int id, String prevBlockHash, int zeroPrefix) {
        this.zeroPrefix = zeroPrefix;
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

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private String generateHash() {
        LocalTime startTime = LocalTime.now();

        Random random = new Random();
        int upperBound = 100_000_000;
        int lowerBound = 10_000_000;
        magicNumber = random.nextInt(upperBound - lowerBound) + lowerBound;
//        magicNumber = random.nextInt(upperBound);

        StringBuilder sb = new StringBuilder();
        String hashString = String.valueOf(sb.append(id).append(timestamp).append(prevBlockHash));

//        System.out.println("String to be hashed: " + sb.toString());
//        System.out.println("hashstring: " + hashString);
        String hash = StringUtil.applySha256(magicNumber + hashString);
//        String pattern = String.format("^0{%d}[^0].*", zeroPrefix);
//        while (!hash.matches(pattern))
        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < zeroPrefix; i++) {
            patternBuilder.append('0');
        }
        String pattern = patternBuilder.toString();
        long count = 0;
        while (!hash.startsWith(pattern)) {
            count++;
//            System.out.println("hash.substring(0, zeroPrefix -1): " + hash.substring(0, zeroPrefix));

            magicNumber = random.nextInt(upperBound - lowerBound) + lowerBound;
//            magicNumber = random.nextInt(upperBound);
//            hashString = String.valueOf(sb.append(id).append(timestamp).append(prevBlockHash).append(magicNumber));
//            System.out.println(sb.toString());
//            hash = StringUtil.applySha256(sb.toString());
            hash = StringUtil.applySha256(magicNumber + hashString);
//            System.out.println(hash);
        }

        generationTime = LocalTime.now().toSecondOfDay() - startTime.toSecondOfDay();
//        System.out.println("Block " + id + " complete.");
//        System.out.println("Count: " + count);
        return hash;
    }

    @Override
    public String toString() {

        return "Block:\n"
                + "Id: " + id
                + "\nTimestamp: " + timestamp
                + "\nMagic number: " + magicNumber
                + "\nHash of the previous block:\n" + prevBlockHash
                + "\nHash of the block:\n" + hash
                + "\nBlock was generating for " + generationTime + " seconds";
    }
}
