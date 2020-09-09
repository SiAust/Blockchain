package io.github.siaust.Model;

import io.github.siaust.Exception.InvalidBlockchain;
import io.github.siaust.Utils.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Blockchain implements Serializable {

    private static final long serialVersionUID = -6810975215568720649L;
    private Block[] blockchain;
    private int zeroPrefix;
    private final String FILEPATH = ".\\blockchain.data";

    public Blockchain() {
    }

    public void initialise() {

        if (deserialize()) return;

        inputZeroPrefix();
//        System.out.println("Enter how many zeros the hash must start with: ");
//        zeroPrefix = new Scanner(System.in).nextInt();

        this.blockchain = new Block[10];
//        addBlock(1); // creates initial starting block

    }

    public void createBlocks() {
        addBlock(10);
        if (!serialize()) System.out.println("Serialization failed.");
    }

    private boolean serialize() {
        try {
            SerializationUtils.serialize(this, FILEPATH);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean deserialize() {
        try {
            Blockchain deserialized = (Blockchain) SerializationUtils.deserialize(FILEPATH);
            blockchain = deserialized.getBlockchain().clone();
//            zeroPrefix = deserialized.zeroPrefix;
            inputZeroPrefix();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No blockchain found on device.");
//            e.printStackTrace();
            return false;
        }
    }

    private void inputZeroPrefix() {
        System.out.println("Enter how many zeros the hash must start with: ");
        try (Scanner scanner = new Scanner(System.in)) {
            zeroPrefix = scanner.nextInt();
        }
    }

    public Block[] getBlockchain() {
        return this.blockchain;
    }

    public void addBlock(int repetitions) {

        for (int i = 0; i < repetitions; i++) {
            if (findLastBlock() == null) {
                blockchain[0] = new Block(1, "0", zeroPrefix);
            } else {
                if (findLastBlock().getId() == blockchain.length) {
                    resizeArray();
                }
                blockchain[findLastBlock().getId()] = new Block(
                        findLastBlock().getId() + 1,
                        findLastBlock().getHash(),
                        zeroPrefix);
            }
        }
    }

    public boolean validate() throws InvalidBlockchain { // todo: implement
        for (int i = blockchain.length - 1; i > 0; i--) {
            if (!blockchain[i].getPrevBlockHash().equals(blockchain[i - 1].getHash())) {

//                throw new InvalidBlockchain("block @ index " + i  + " does not validate with" +
//                        "block @ index " + (i -1));

                return false;
            }
        }
        return true;
    }

    private Block findLastBlock() {
        Block block = null;
        for (Block element : blockchain) {
            if (element == null) {
                break;
            }
            block = element;
        }
        return block;
    }

    private void resizeArray() {
        Block[] resizedBlockArray = new Block[blockchain.length + 10];
        System.arraycopy(blockchain, 0, resizedBlockArray, 0, blockchain.length);
        blockchain = resizedBlockArray.clone();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Block block : blockchain) {
            if (block != null) {
                stringBuilder.append(block.toString()).append("\n\n");
            }
        }
        return stringBuilder.toString();
    }
}
