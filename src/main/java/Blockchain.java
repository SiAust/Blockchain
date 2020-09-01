public class Blockchain {

    private final Block[] blockchain;

    public Blockchain() {
        this.blockchain = new Block[10];
        addBlock(); // creates initial starting block
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

    public boolean validate() {
        for (int i = blockchain.length -1; i > 0; i--) {
            if (!blockchain[i].getPrevBlockHash().equals(blockchain[i - 1].getHash())) {
                return false;
            }
        }
        return true;
    }

    public void printValidation() {
        for (int i = blockchain.length -1; i > 0; i--) {
            System.out.printf("block id: %d prevHash: %s" + //todo: fix first line offset
                            "\nblock id: %d hash: %68s\n\n",
                    (i + 1),
                    blockchain[i].getPrevBlockHash(),
                    i,
                    blockchain[i - 1].getHash());
        }
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
