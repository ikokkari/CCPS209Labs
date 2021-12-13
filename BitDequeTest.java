import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class BitDequeTest {

    @Test public void testOneThousand() {
        testWithTortoiseAndHare(1000);
    }

    @Test public void testTenThousand() {
        testWithTortoiseAndHare(10_000);
    }

    @Test public void testHundredThousand() {
        testWithTortoiseAndHare(100_000);
    }

    private void testWithTortoiseAndHare(int n) throws IllegalStateException {
        // These two BitDeque instances are supposed to be in lockstep at all times.
        // Same elements are pushed and popped in both, just from different ends.
        BitDeque bd1 = new BitDeque(); // Used for pushFront and popBack.
        BitDeque bd2 = new BitDeque(); // Used for pushBack and popFront.
        Random tortoiseBits = new Random(n); // Separate but equal RNG's for tortoise and hare
        Random hareBits = new Random(n);     // that advance in lockstep to create random bits.
        Random stepper = new Random(12345); // Separate RNG to produce step sizes for both.
        int stepSize = 10, count = 0, goal = 10;
        int expectedSize = 0;
        for(int round = 0; round < n; round++) {
            int hareSteps = 1 + stepper.nextInt(stepSize);
            // The hare keeps pushing bits in from one end.
            for(int h = 0; h < hareSteps; h++) {
                boolean bit = hareBits.nextBoolean();
                bd1.pushFront(bit);
                bd2.pushBack(bit);
                expectedSize++;
                // The two boolean deques must be in lockstep at all times.
                assertEquals(expectedSize, bd1.getSize());
                assertEquals(expectedSize, bd2.getSize());
                // The logic of resizing must be the same both left and right.
                assertEquals(bd1.getCapacity(), bd2.getCapacity());
            }
            // The tortoise keeps popping the same bits out from the other end. Since the step
            // accumulation of tortoise is capped to equal at most that of the hare, the random
            // walk of their distances will reach arbitrary heights, and yet jump up and down
            // to trigger both expansions and contractions.
            int tortoiseSteps = Math.min(expectedSize, 1 + stepper.nextInt(stepSize));
            for(int t = 0; t < tortoiseSteps; t++) {
                boolean expectedBit = tortoiseBits.nextBoolean();
                boolean b1 = bd1.popBack();
                assertEquals(expectedBit, b1);
                boolean b2 = bd2.popFront();
                assertEquals(expectedBit, b2);
                expectedSize--;
                // The two boolean deques must be in lockstep at all times.
                assertEquals(expectedSize, bd1.getSize());
                assertEquals(expectedSize, bd2.getSize());
                // The logic of resizing must be the same both left and right.
                assertEquals(bd1.getCapacity(), bd2.getCapacity());
            }
            // Increase the random step amplitude at exponentially increasing intervals.
            if(++count == goal) {
                count = 0;
                goal = 2 * goal;
                stepSize = 3 * (stepSize / 2);
            }
        }
    }
}