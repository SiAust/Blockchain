package io.github.siaust.View;

import io.github.siaust.Model.Block;
import io.github.siaust.Model.Blockchain;

public class BlockchainView {

    public void printModel(Blockchain model) {
        System.out.println(model);
    }

    public void printValidation(Blockchain model) {
        Block[] blockchain = model.getBlockchain();
        for (int i = blockchain.length - 1; i > 0; i--) {
            System.out.printf("block id: %d prevHash: %s" + //fixme: works only for array size 10
                            "\nblock id: %d hash: %" + (i % 9 == 0 ? 69 : 68) + "s\n\n", // pads the hashes
                    (i + 1), blockchain[i] == null ? "null" : blockchain[i].getPrevBlockHash(),
                    i, blockchain[i] == null ? "null" : blockchain[i - 1].getHash());
        }
    }

    public void printMessages() {
        Blockchain.getTransactionQueue().forEach(System.out::println);
    }
}
