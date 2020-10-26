package io.github.siaust.Utils;

import io.github.siaust.Model.Block;
import io.github.siaust.Model.Miner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MinerExecutor {

    public static Block mineBlocks(int zeroPrefix, Block previousBlock) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        Block block = null;
        while (block == null) {
            List<Miner> collection = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                collection.add(new Miner(zeroPrefix, previousBlock));
            }
            try {
                block = executor.invokeAny(collection);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdownNow();
//        System.out.println("is terminated: " + executor.isTerminated()); // fixme debug

        return block;
    }
}
