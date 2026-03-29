import org.junit.Test;
import java.util.Iterator;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrimeGensTest {

    private static final int[] TWIN_PRIMES_PREFIX = {
            3, 5, 11, 17, 29, 41, 59, 71, 101, 107, 137, 149, 179, 191, 197, 227, 239, 269, 281, 311
    };

    private static final int[] SAFE_PRIMES_PREFIX = {
            5, 7, 11, 23, 47, 59, 83, 107, 167, 179, 227, 263, 347, 359, 383, 467, 479, 503, 563, 587, 719, 839, 863
    };

    private static final int[] STRONG_PRIMES_PREFIX = {
            11, 17, 29, 37, 41, 59, 67, 71, 79, 97, 101, 107, 127, 137, 149, 163, 179, 191, 197, 223, 227, 239, 251
    };

    // Helper to extract a prefix from an iterator.
    private int[] extractPrefix(Iterator<Integer> it, int n) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            assertTrue("hasNext() should return true for infinite iterator", it.hasNext());
            result[i] = it.next();
        }
        return result;
    }

    // Helper for the CRC mass test over an iterator.
    private void massTest(Iterator<Integer> it, int n, long expected) {
        CRC32 check = new CRC32();
        for (int i = 0; i < n; i++) {
            check.update(it.next());
        }
        assertEquals(expected, check.getValue());
    }

    // --- Twin Primes ---

    @Test public void testTwinPrimesPrefix() {
        assertArrayEquals(TWIN_PRIMES_PREFIX,
                extractPrefix(new PrimeGens.TwinPrimes(), TWIN_PRIMES_PREFIX.length));
    }

    @Test public void testTwinPrimesMass() {
        massTest(new PrimeGens.TwinPrimes(), 3500, 2941193748L);
    }

    // --- Safe Primes ---

    @Test public void testSafePrimesPrefix() {
        assertArrayEquals(SAFE_PRIMES_PREFIX,
                extractPrefix(new PrimeGens.SafePrimes(), SAFE_PRIMES_PREFIX.length));
    }

    @Test public void testSafePrimesMass() {
        massTest(new PrimeGens.SafePrimes(), 3000, 3874618335L);
    }

    // --- Strong Primes ---

    @Test public void testStrongPrimesPrefix() {
        assertArrayEquals(STRONG_PRIMES_PREFIX,
                extractPrefix(new PrimeGens.StrongPrimes(), STRONG_PRIMES_PREFIX.length));
    }

    @Test public void testStrongPrimesMass() {
        massTest(new PrimeGens.StrongPrimes(), 15_000, 494629196L);
    }
}