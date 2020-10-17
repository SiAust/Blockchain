import io.github.siaust.Model.Blockchain;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockchainTest {

    Blockchain blockchain = new Blockchain();

    // todo: implement tests

    @Test
    public void testGenerationTime() {
        assertEquals("Generation time greater than 15 seconds zeroPrefix stays the same, 0 returned",
                    0, blockchain.setZeroPrefix(16));
        assertEquals("Generation time less than 15 seconds should increment zeroPrefix by 1",
                        1, blockchain.setZeroPrefix(0));
        assertEquals("Generation time greater than 60 seconds should decrement zeroPrefix by 1",
                -1, blockchain.setZeroPrefix(61));
    }

}
