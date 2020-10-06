package io.github.siaust;

import io.github.siaust.Controller.BlockchainController;
import io.github.siaust.Model.Blockchain;
import io.github.siaust.View.BlockchainView;

public class Main {
    public static void main(String[] args) {

        Blockchain model = new Blockchain();
        BlockchainView view = new BlockchainView();

        BlockchainController controller = new BlockchainController(model, view);
        controller.init();
    }
}
