import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class BitDequeTest {

    private static final int MIN_CAPACITY = 512;

    // --- Basic operations ---

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

    @Test public void testInitialState() {
        BitDeque bd = new BitDeque();
        assertEquals(0, bd.getSize());
        assertEquals(MIN_CAPACITY, bd.getCapacity());
    }

    @Test public void testSinglePushPopFront() {
        BitDeque bd = new BitDeque();
        bd.pushFront(true);
        assertEquals(1, bd.getSize());
        assertEquals(true, bd.popFront());
        assertEquals(0, bd.getSize());
    }

    @Test public void testSinglePushPopBack() {
        BitDeque bd = new BitDeque();
        bd.pushBack(false);
        assertEquals(1, bd.getSize());
        assertEquals(false, bd.popBack());
        assertEquals(0, bd.getSize());
    }

    @Test public void testPushFrontPopBack() {
        // FIFO behavior: push front, pop back
        BitDeque bd = new BitDeque();
        bd.pushFront(true);
        bd.pushFront(false);
        bd.pushFront(true);
        // Deque from back to front: true, false, true
        // Pop back gives FIFO order: true (first pushed), false, true (last pushed)
        assertEquals(true, bd.popBack());
        assertEquals(false, bd.popBack());
        assertEquals(true, bd.popBack());
    }

    @Test public void testPushBackPopFront() {
        // FIFO behavior: push back, pop front
        BitDeque bd = new BitDeque();
        bd.pushBack(true);
        bd.pushBack(false);
        bd.pushBack(true);
        assertEquals(true, bd.popFront());
        assertEquals(false, bd.popFront());
        assertEquals(true, bd.popFront());
    }

    @Test public void testStackBehaviorFront() {
        // LIFO: push front, pop front
        BitDeque bd = new BitDeque();
        bd.pushFront(true);
        bd.pushFront(false);
        assertEquals(false, bd.popFront()); // last in, first out
        assertEquals(true, bd.popFront());
    }

    @Test public void testStackBehaviorBack() {
        // LIFO: push back, pop back
        BitDeque bd = new BitDeque();
        bd.pushBack(true);
        bd.pushBack(false);
        assertEquals(false, bd.popBack()); // last in, first out
        assertEquals(true, bd.popBack());
    }

    // --- Exception on empty pop ---

    @Test public void testPopFrontEmptyThrows() {
        BitDeque bd = new BitDeque();
        assertThrows(IllegalStateException.class, bd::popFront);
    }

    @Test public void testPopBackEmptyThrows() {
        BitDeque bd = new BitDeque();
        assertThrows(IllegalStateException.class, bd::popBack);
    }

    @Test public void testPopAfterDrainThrows() {
        BitDeque bd = new BitDeque();
        bd.pushBack(true);
        bd.popBack();
        assertThrows(IllegalStateException.class, bd::popFront);
    }

    // --- Expansion ---

    @Test public void testExpansionOnPushBack() {
        // Push enough elements to force expansion beyond MIN_CAPACITY
        BitDeque bd = new BitDeque();
        for (int i = 0; i < MIN_CAPACITY; i++) {
            bd.pushBack(i % 2 == 0);
        }
        assertTrue(bd.getCapacity() > MIN_CAPACITY);
        assertEquals(MIN_CAPACITY, bd.getSize());
        // All elements still correct (FIFO)
        for (int i = 0; i < MIN_CAPACITY; i++) {
            assertEquals(i % 2 == 0, bd.popFront());
        }
    }

    @Test public void testExpansionOnPushFront() {
        BitDeque bd = new BitDeque();
        for (int i = 0; i < MIN_CAPACITY; i++) {
            bd.pushFront(i % 3 == 0);
        }
        assertTrue(bd.getCapacity() > MIN_CAPACITY);
        assertEquals(MIN_CAPACITY, bd.getSize());
        // Pop back gives FIFO order (first pushed comes out last from front)
        for (int i = 0; i < MIN_CAPACITY; i++) {
            assertEquals(i % 3 == 0, bd.popBack());
        }
    }

    // --- Contraction ---

    @Test public void testContractionAfterManyPops() {
        // Push many, then pop most: capacity should shrink
        BitDeque bd = new BitDeque();
        int count = MIN_CAPACITY * 4;
        for (int i = 0; i < count; i++) {
            bd.pushBack(true);
        }
        int bigCapacity = bd.getCapacity();
        assertTrue(bigCapacity > MIN_CAPACITY);
        // Pop almost everything
        for (int i = 0; i < count - 10; i++) {
            bd.popFront();
        }
        // Capacity should have contracted
        assertTrue(bd.getCapacity() < bigCapacity);
        assertEquals(10, bd.getSize());
    }

    @Test public void testCapacityNeverBelowMinimum() {
        BitDeque bd = new BitDeque();
        bd.pushBack(true);
        bd.popBack();
        assertEquals(MIN_CAPACITY, bd.getCapacity());
        // Even after push/pop cycles at small size
        for (int i = 0; i < 100; i++) {
            bd.pushFront(true);
            bd.popFront();
        }
        assertEquals(MIN_CAPACITY, bd.getCapacity());
    }

    // --- Capacity invariant ---

    @Test public void testCapacityInvariant() {
        // Unless at minimum, capacity <= 4 * size
        BitDeque bd = new BitDeque();
        Random rng = new Random(42);
        int size = 0;
        for (int i = 0; i < 5000; i++) {
            if (size == 0 || rng.nextInt(3) < 2) {
                bd.pushBack(rng.nextBoolean());
                size++;
            } else {
                bd.popFront();
                size--;
            }
            assertEquals(size, bd.getSize());
            assertTrue(bd.getCapacity() == MIN_CAPACITY || bd.getCapacity() <= 4 * bd.getSize(),
                    "Capacity " + bd.getCapacity() + " > 4 * size " + bd.getSize());
        }
    }

    // --- Symmetry: front and back operations produce same capacity behavior ---

    @Test public void testFrontBackSymmetry() {
        BitDeque bdFront = new BitDeque();
        BitDeque bdBack = new BitDeque();
        Random rng = new Random(99);
        int size = 0;
        for (int i = 0; i < 2000; i++) {
            if (size == 0 || rng.nextInt(3) < 2) {
                boolean bit = rng.nextBoolean();
                bdFront.pushFront(bit);
                bdBack.pushBack(bit);
                size++;
            } else {
                bdFront.popBack();
                bdBack.popFront();
                size--;
            }
            assertEquals(bdFront.getCapacity(), bdBack.getCapacity(),
                    "Capacities diverged at step " + i);
        }
    }

    // --- Mixed operations ---

    @Test public void testAlternatingPushPop() {
        BitDeque bd = new BitDeque();
        // Alternating push/pop should not cause unbounded growth
        for (int i = 0; i < 10000; i++) {
            bd.pushBack(i % 2 == 0);
            bd.pushBack(i % 3 == 0);
            bd.popFront();
        }
        assertEquals(10000, bd.getSize());
        // Capacity should be reasonable
        assertTrue(bd.getCapacity() <= 4 * bd.getSize());
    }

    @Test public void testBothEndsMixed() {
        // Push from both ends, pop from both ends
        BitDeque bd = new BitDeque();
        bd.pushFront(true);   // [T]
        bd.pushBack(false);   // [T, F]
        bd.pushFront(false);  // [F, T, F]
        bd.pushBack(true);    // [F, T, F, T]
        assertEquals(4, bd.getSize());
        assertEquals(false, bd.popFront());  // F
        assertEquals(true, bd.popBack());    // T
        assertEquals(true, bd.popFront());   // T
        assertEquals(false, bd.popBack());   // F
        assertEquals(0, bd.getSize());
    }

    // --- CRC mass tests ---

    @Test public void testOneThousand() {
        testWithTortoiseAndHare(1000);
    }

    @Test public void testTenThousand() {
        testWithTortoiseAndHare(10_000);
    }

    @Test public void testHundredThousand() {
        testWithTortoiseAndHare(100_000);
    }

    private void verifyAssertions(BitDeque bd1, BitDeque bd2, int expectedSize) {
        assertEquals(expectedSize, bd1.getSize());
        assertEquals(expectedSize, bd2.getSize());
        assertEquals(bd1.getCapacity(), bd2.getCapacity());
        assertTrue(bd1.getCapacity() == MIN_CAPACITY || bd1.getCapacity() <= 4 * bd1.getSize());
        assertTrue(bd2.getCapacity() == MIN_CAPACITY || bd2.getCapacity() <= 4 * bd2.getSize());
    }

    private void testWithTortoiseAndHare(int n) throws IllegalStateException {
        BitDeque bd1 = new BitDeque();
        BitDeque bd2 = new BitDeque();
        Random tortoiseBits = new Random(n);
        Random hareBits = new Random(n);
        Random stepper = new Random(12345);
        int stepSize = 10, count = 0, goal = 10;
        int expectedSize = 0;
        for(int round = 0; round < n; round++) {
            int hareSteps = 1 + stepper.nextInt(stepSize);
            for(int h = 0; h < hareSteps; h++) {
                boolean bit = hareBits.nextBoolean();
                bd1.pushFront(bit);
                bd2.pushBack(bit);
                expectedSize++;
                verifyAssertions(bd1, bd2, expectedSize);
            }
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
            if(++count == goal) {
                count = 0;
                goal = 2 * goal;
                stepSize = 3 * (stepSize / 2);
            }
        }
    }
}