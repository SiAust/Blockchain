

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        int i = 0;
        while (i < 9) { // we've already created one block on init of Blockchain
            blockchain.addBlock();
            i++;
        }

        System.out.println(blockchain);
    }

}
