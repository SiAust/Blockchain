public class Blockchain {

    private final Block[] blockchain;

    public Blockchain() {
        this.blockchain = new Block[10];
        addBlock(); // creates initial starting block?
    }

    public void addBlock() {
        if (findLastBlock() == null) {
            blockchain[0] = new Block(1, "0");
        } else {
            blockchain[findLastBlock().getId()] = new Block(
                    findLastBlock().getId() + 1,
                    findLastBlock().getHash()
            );
        }
    }

    private boolean validate() {
        // todo: validate the blockchain
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
