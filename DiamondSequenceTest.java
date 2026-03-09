import org.junit.Test;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiamondSequenceTest {

    // --- Explicit tests ---

    @Test public void testHasNextAlwaysTrue() {
        Iterator<Integer> it = new DiamondSequence();
        for(int i = 0; i < 100; i++) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertTrue(it.hasNext());
    }

    @Test public void testKnownInitialPrefix() {
        Iterator<Integer> it = new DiamondSequence();
        int[] expected = {
                1, 3, 2, 6, 8, 4, 11, 5, 14, 16,
                7, 19, 21, 9, 24, 10, 27, 29, 12, 32,
                13, 35, 37, 15, 40, 42, 17, 45, 18, 48
        };
        for(int i = 0; i < expected.length; i++) {
            assertEquals("Element at position " + (i + 1),
                    Integer.valueOf(expected[i]), it.next());
        }
    }

    @Test public void testDivisibilityProperty() {
        // The defining property: sum of first k elements is divisible by k.
        Iterator<Integer> it = new DiamondSequence();
        long sum = 0;
        for(int k = 1; k <= 1000; k++) {
            sum += it.next();
            assertEquals("Sum at position " + k + " must be divisible by " + k,
                    0L, sum % k);
        }
    }

    @Test public void testNoDuplicatesInFirstThousand() {
        Iterator<Integer> it = new DiamondSequence();
        Set<Integer> seen = new HashSet<>();
        for(int k = 0; k < 1000; k++) {
            int v = it.next();
            assertTrue("Duplicate value " + v + " at position " + (k + 1),
                    seen.add(v));
        }
    }

    @Test public void testSelfReferentiality() {
        // If seq[k] == v, then seq[v] == k. All cycles have length 2.
        Map<Integer, Integer> mustBe = new HashMap<>();
        Iterator<Integer> it = new DiamondSequence();
        for(int k = 1; k < 1_000_000; k++) {
            int v = it.next();
            if(mustBe.containsKey(k)) {
                assertEquals(v, (long) mustBe.get(k));
                mustBe.remove(k);
            }
            if(v > k) {
                mustBe.put(v, k);
            }
        }
    }

    // --- Mass / CRC tests ---

    @Test public void testFirstMillion() {
        int count = 1_000_000;
        CRC32 check = new CRC32();
        Iterator<Integer> it = new DiamondSequence();
        while(--count > 0) {
            int i = it.next();
            if(count % 1000 == 0) { check.update(i); }
        }
        assertEquals(2833073996L, check.getValue());
    }

    @Test public void testFirstHundredMillion() {
        int count = 100_000_000;
        CRC32 check = new CRC32();
        Iterator<Integer> it = new DiamondSequence();
        while(--count > 0) {
            int i = it.next();
            if(count % 1000 == 0) { check.update(i); }
        }
        assertEquals(2546749209L, check.getValue());
    }
}