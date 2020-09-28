package io.github.siaust.Controller;

import io.github.siaust.Model.Blockchain;
import io.github.siaust.View.BlockchainView;

public class BlockchainController {

    private final Blockchain model;
    private final BlockchainView view;

    public BlockchainController(Blockchain model, BlockchainView view) {
        this.model = model;
        this.view = view;
    }

    public void initialiseBlockchain() {
        model.initialise();
    }

    public void mineBlockchain() {
        model.createBlocks();
    }

    public void updateView() {
        view.printModel(model);
    }

    public void printBlockchainValidation() {
        view.printValidation(model);
    }

    public void printBlockchainMessages() {view.printMessages();}

}
