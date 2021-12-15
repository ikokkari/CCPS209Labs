import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class BitDequeTest {

    @Test public void testExplicit() {
        BitDeque bd = new BitDeque();
        assertEquals(0, bd.getSize());
        bd.pushBack(true);
        assertEquals(1, bd.getSize());
        bd.pushFront(false);
        bd.pushFront(true);
        assertEquals(3, bd.getSize());
        assertEquals(true, bd.popFront());
        assertEquals(true, bd.popBack());
        assertEquals(false, bd.popFront());
        assertEquals(0, bd.getSize());
    }

    @Test public void testOneThousand() {
        testWithTortoiseAndHare(1000);
    }

    @Test public void testTenThousand() {
        testWithTortoiseAndHare(10_000);
    }

    @Test public void testHundredThousand() {
        testWithTortoiseAndHare(100_000);
    }

    private static final int MIN_CAPACITY = 512;

    private void verifyAssertions(BitDeque bd1, BitDeque bd2, int expectedSize) {
        // The two boolean deques must be in lockstep at all times.
        assertEquals(expectedSize, bd1.getSize());
        assertEquals(expectedSize, bd2.getSize());
        // The logic of resizing must be the same both front and back.
        assertEquals(bd1.getCapacity(), bd2.getCapacity());
        // Unless at minimum capacity, the capacity may not be more than
        // four times the current number of elements in the queue.
        assertTrue(bd1.getCapacity() == MIN_CAPACITY || bd1.getCapacity() <= 4 * bd1.getSize());
        assertTrue(bd2.getCapacity() == MIN_CAPACITY || bd2.getCapacity() <= 4 * bd2.getSize());
    }

    private void testWithTortoiseAndHare(int n) throws IllegalStateException {
        // These two BitDeque instances are supposed to be in lockstep at all times.
        // Same elements are pushed and popped in both, just from different ends.
        BitDeque bd1 = new BitDeque(); // Used for pushFront and popBack.
        BitDeque bd2 = new BitDeque(); // Used for pushBack and popFront.
        Random tortoiseBits = new Random(n); // Separate but equal RNG's for tortoise and hare
        Random hareBits = new Random(n);     // that advance in lockstep to create same bits.
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
                verifyAssertions(bd1, bd2, expectedSize);
            }
            // The tortoise keeps popping the same bits out from the other end. Since the step
            // accumulation of tortoise is capped to equal at most that of the hare, the random
            // walk of their distances will reach arbitrary heights, and still jump up and down
            // to trigger both expansions and contractions.
            int tortoiseSteps = Math.min(expectedSize, 1 + stepper.nextInt(stepSize));
            for(int t = 0; t < tortoiseSteps; t++) {
                boolean expectedBit = tortoiseBits.nextBoolean();
                boolean b1 = bd1.popBack();
                assertEquals(expectedBit, b1);
                boolean b2 = bd2.popFront();
                assertEquals(expectedBit, b2);
                expectedSize--;
                verifyAssertions(bd1, bd2, expectedSize);
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