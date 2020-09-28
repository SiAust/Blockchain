package io.github.siaust.Model;

import java.util.concurrent.Callable;

public class Miner implements Callable<Block> {

    private final Block previousBlock;
    private final int zeroPrefix;

    /** This class implements Callable interface. Blocks are generated (mined) for the Blockchain
     * in the Call() method and therefore can be run in a new Thread. */
    public Miner(int zeroPrefix, Block previousBlock) {
        this.zeroPrefix = zeroPrefix;
        this.previousBlock = previousBlock;
    }

    @Override
    public Block call() {
        try {
            return new Block(previousBlock == null ? 1 : previousBlock.getId() + 1
                    , previousBlock == null ? "0" : previousBlock.getHash()
                    , zeroPrefix
                    , Thread.currentThread().getName().split("-")[3]);
        } catch (InterruptedException e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
        }
        return null;
    }
}
