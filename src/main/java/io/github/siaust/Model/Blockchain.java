package io.github.siaust.Model;

import io.github.siaust.Exception.InvalidBlockchain;
import io.github.siaust.Model.VirtualCoin.Accounts;
import io.github.siaust.Model.VirtualCoin.Transaction;
import io.github.siaust.Utils.MinerExecutor;
import io.github.siaust.Utils.SerializationUtils;
import io.github.siaust.Utils.Server;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Blockchain implements Serializable {

    private static final long serialVersionUID = -6810975215568720649L;

    private Block[] blockchain;
    private int zeroPrefix = 0;
    private final String FILEPATH = ".\\blockchain.data";

    public static volatile Queue<Transaction> transactions = new ArrayDeque<>();

    private static final Random random = new Random();
    private volatile int messageID = 1;
    private static int msgIDStatic;

    public static Accounts accounts;

    public Blockchain() { // todo: convert to singleton, we don't want multiple instances of our Blockchain!
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
            accounts.serializeAccounts();
            messageID = msgIDStatic;
            SerializationUtils.serialize(this, FILEPATH);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deserialize() {
        accounts = new Accounts();
        try {
            Blockchain deserialized = (Blockchain) SerializationUtils.deserialize(FILEPATH);
            blockchain = deserialized.getBlockchain().clone();
            zeroPrefix = deserialized.zeroPrefix;
            messageID = deserialized.messageID;
            msgIDStatic = messageID; // so we can use the static method, and still serialize our msgID
            System.out.println("Blockchain deserialized");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No blockchain found");
            return false;
        }
    }

    public Block[] getBlockchain() {
        return this.blockchain;
    }

    public void addBlock(int repetitions) {
        Server server = new Server(transactions, accounts);
        server.start();

        for (int i = 0; i < repetitions; i++) {
            Block block;
            /* no blocks exist, serialization hasn't happened yet */
            if (findLastBlock() == null) {
                block = MinerExecutor.mineBlocks(zeroPrefix, null); // todo validate this block
                blockchain[0] = block;
                setZeroPrefix(block.getGenerationTime());
                block.getTransactionList().forEach(Transaction::completeTransaction);
            } else {
                if (findLastBlock().getId() == blockchain.length) {
                    resizeArray();
                }
                block = MinerExecutor.mineBlocks(zeroPrefix, findLastBlock());
                try {
                    if (isValid(block)) {
                        blockchain[findLastBlock().getId()] = block;
                        setZeroPrefix(findLastBlock().getGenerationTime());
                        /* carry out transactions as block is valid and placed into blockchain */
                        block.getTransactionList().forEach(Transaction::completeTransaction);
                        block.getTransactionList().forEach(System.out::println);
                    }
                } catch (InvalidBlockchain e) {
                    System.out.println(e.getMessage());
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
            List<Transaction> transactionList = block.getTransactionList();
            for (int i = 0; i < transactionList.size() - 1; i++) {
                if (!(transactionList.get(i).getMessageID() < transactionList.get(i + 1).getMessageID())) {
                    throw new InvalidBlockchain("Message ID invalid");
                }
            }
            return true;
        } else {
            throw new InvalidBlockchain("Block hashes invalid");
        }
    }

    public static synchronized int getMessageID() {
        return msgIDStatic = random.nextInt(10) + 1 + msgIDStatic;
    }

    public static Queue<Transaction> getTransactionQueue() {
        return transactions;
    }

    /** Sets the zeroPrefix field according to the length of time a Block is generating.
     * This should stabilise the generation time to prevent exponential growth of
     * generating a Block. Replaces user input defined zeroPrefix. */
    public void setZeroPrefix(float generationTime) {
        if (generationTime < 1) { // 1/100th sec
            zeroPrefix += 1;
        }
        if (generationTime > 5) { // 1/10th sec
            if (zeroPrefix > 0) {
                zeroPrefix -= 1;
            }
        }
//        System.out.println("Generation time: " + (float) generationTime/1000000000 );
        System.out.println("The zero prefix is set to " + zeroPrefix);
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
