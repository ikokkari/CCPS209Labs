import org.junit.Test;
import java.util.Iterator;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PrimeGensTest {

    // Expected prefixes of the infinite sequences of twin primes, safe primes and strong primes.

    private static final int[] TWIN_PRIMES_PREFIX = {
            3, 5, 11, 17, 29, 41, 59, 71, 101, 107, 137, 149, 179, 191, 197, 227, 239, 269, 281, 311
    };

    private static final int[] SAFE_PRIMES_PREFIX = {
            5, 7, 11, 23, 47, 59, 83, 107, 167, 179, 227, 263, 347, 359, 383, 467, 479, 503, 563, 587, 719, 839, 863
    };

    private static final int[] STRONG_PRIMES_PREFIX = {
            11, 17, 29, 37, 41, 59, 67, 71, 79, 97, 101, 107, 127, 137, 149, 163, 179, 191, 197, 223, 227, 239, 251
    };

    @Test public void testTwinPrimes() {
        Iterator<Integer> it = new PrimeGens.TwinPrimes();
        int[] result = new int[TWIN_PRIMES_PREFIX.length];
        for(int i = 0; i < TWIN_PRIMES_PREFIX.length; i++) {
            result[i] = it.next();
        }
        assertArrayEquals(TWIN_PRIMES_PREFIX, result);

        CRC32 check = new CRC32();
        it = new PrimeGens.TwinPrimes();
        for(int i = 0; i < 3500; i++) {
            check.update(it.next());            
        }
        assertEquals(2941193748L, check.getValue());
    }
    
    @Test public void testSafePrimes() {
        Iterator<Integer> it = new PrimeGens.SafePrimes();
        int[] result = new int[SAFE_PRIMES_PREFIX.length];
        for(int i = 0; i < SAFE_PRIMES_PREFIX.length; i++) {
            result[i] = it.next();
        }
        assertArrayEquals(SAFE_PRIMES_PREFIX, result);

        CRC32 check = new CRC32();
        it = new PrimeGens.SafePrimes();
        for(int i = 0; i < 3000; i++) {
            check.update(it.next());            
        }
        assertEquals(3874618335L, check.getValue());
    }
    
    @Test public void testStrongPrimes() {
        Iterator<Integer> it = new PrimeGens.StrongPrimes();
        int[] result = new int[STRONG_PRIMES_PREFIX.length];
        for(int i = 0; i < STRONG_PRIMES_PREFIX.length; i++) {
            result[i] = it.next();
        }
        assertArrayEquals(STRONG_PRIMES_PREFIX, result);

        CRC32 check = new CRC32();
        it = new PrimeGens.StrongPrimes();
        for(int i = 0; i < 15_000; i++) {
            check.update(it.next());            
        }
        assertEquals(494629196L, check.getValue());
    }
    
}
