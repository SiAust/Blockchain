package io.github.siaust.Model;

import io.github.siaust.Exception.InvalidBlockchain;
import io.github.siaust.Utils.MinerExecutor;
import io.github.siaust.Utils.SerializationUtils;
import io.github.siaust.Utils.Server;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.function.Supplier;

public class Blockchain implements Serializable {

    private static final long serialVersionUID = -6810975215568720649L;

    private Block[] blockchain;
    private int zeroPrefix = 0;
    private final String FILEPATH = ".\\blockchain.data";

    public static volatile Queue<Message> messagesList = new ArrayDeque<>();
    private final Random random = new Random();
    private volatile int messageID = 1;

    public Blockchain() {
    }

    public void initialise() {
        if (deserialize()) return;
        this.blockchain = new Block[10];
    }

    public void createBlocks() {
        addBlock(5);
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
            zeroPrefix = deserialized.zeroPrefix;
            messageID = deserialized.messageID;
            System.out.println("Deserialized");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No blockchain found on device.");
            return false;
        }
    }

    public Block[] getBlockchain() {
        return this.blockchain;
    }

    public void addBlock(int repetitions) {
        Server server = new Server(messagesList, messageIDIncrementer());
        server.start();

        for (int i = 0; i < repetitions; i++) {
            if (findLastBlock() == null) {  /* no blocks exist, serialization hasn't happened yet */
                blockchain[0] = MinerExecutor.mineBlocks(zeroPrefix, null);
                zeroPrefix += setZeroPrefix(blockchain[0].getGenerationTime());
            } else {
                if (findLastBlock().getId() == blockchain.length) {
                    resizeArray();
                }
                Block block = MinerExecutor.mineBlocks(zeroPrefix, findLastBlock());
                try {
                    if (isValid(block)) {
                        blockchain[findLastBlock().getId()] = block;
                        zeroPrefix += setZeroPrefix(findLastBlock().getGenerationTime());
                    }
                } catch (InvalidBlockchain e) {
                    System.out.println(e.getMessage());
                    // todo: Temp validation solution? If invalid, do ??
                }
            }
        }
        /* try's to interrupt the thread, we check for condition in while loop */
        server.interrupt();
        /* Closes the server if no connections have been made, as the thread will be blocked on serverSocket.accept() */
        server.closeServer();
    }

    /**
     * Validate each block by comparing this Block's previousHash field to the preceding Block's
     * hash field in the Blockchain array. Any Message objects contained in the Block will be checked
     * that the messageID field is > the preceding block. This prevents sending a copy of a message with
     * the same signature, as the messageID will not be in sync.
     * @param block the generated Block must be valid before being placed in the Blockchain
     * @exception InvalidBlockchain exception */
    private boolean isValid(Block block) throws InvalidBlockchain {
        if (block.getPrevBlockHash().equals(blockchain[block.getId() - 2].getHash())) {
            List<Message> messageList = block.getMessageList();
            for (int i = 0; i < messageList.size() - 1; i++) {
                if (!(messageList.get(i).getMessageID() < messageList.get(i + 1).getMessageID())) {
                    throw new InvalidBlockchain("Message ID invalid");
                }
            }
            return true;
        } else {
            throw new InvalidBlockchain("Block hashes invalid");
        }
    }

    public synchronized Supplier<Integer> messageIDIncrementer() {
        return () -> {
            int number = random.nextInt(10) + 1 + messageID; // +1 as nextInt() could return 0
            messageID = number;
            return number;
        };
    }

    public static Queue<Message> getMessageQueue() {
        return messagesList;
    }

    /** Sets the zeroPrefix field according to the length of time a Block is generating.
     * This should stabilise the generation time to prevent exponential growth of
     * generating a Block. Replaces user input defined zeroPrefix. */
    public int setZeroPrefix(int generationTime) {
        if (zeroPrefix >= 6) { // FIXME: 17/10/2020 remove, find another method to keep generation time low
            zeroPrefix = 6;
            return 0;
        }
        if (generationTime < 15) {
//            zeroPrefix++;
            System.out.println("The zero prefix of the hash has been increased to " + zeroPrefix);
            return 1;
        }
        if (generationTime > 60) {
//            zeroPrefix--;
            System.out.println("The zero prefix of the hash has been decreased to " + zeroPrefix);
            return -1;
        }
        System.out.println("The zero prefix of the hash stays the same at " + zeroPrefix);
        return 0;
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
