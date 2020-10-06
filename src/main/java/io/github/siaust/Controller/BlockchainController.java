package io.github.siaust.Controller;

import io.github.siaust.Model.Blockchain;
import io.github.siaust.Utils.Server;
import io.github.siaust.View.BlockchainView;

public class BlockchainController {

    private final Blockchain model;
    private final BlockchainView view;
    private Thread keyRequestServer;

    public BlockchainController(Blockchain model, BlockchainView view) {
        this.model = model;
        this.view = view;
    }

    public void init() {
        initialiseBlockchain();
        keyRequestListener();
        mineBlockchain();
        updateView();
//        printBlockchainValidation();
        printBlockchainMessages();
    }

    public void initialiseBlockchain() {
        model.initialise();
    }

    private void keyRequestListener() {
        keyRequestServer = new Server(6666);
        keyRequestServer.start();
    }

    public void mineBlockchain() {
        model.createBlocks();
    }

    public void updateView() {
        view.printModel(model);
        keyRequestServer.interrupt(); // stop server listening for requests for the public key
    }

    public void printBlockchainValidation() {
        view.printValidation(model);
    }

    public void printBlockchainMessages() {view.printMessages();}

}
