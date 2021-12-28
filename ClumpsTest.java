import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.zip.CRC32;
import java.util.Random;

public class ClumpsTest {

    @Test public void testClumpsExplicit() {
        Clumps c1 = new Clumps(5);
        // All five items are initially singleton clumps.
        assertEquals(1, c1.clumpSize(3));
        assertFalse(c1.sameClump(0, 3));
        // Meld 0 and 3 into same clump.
        c1.meld(0, 3);
        assertTrue(c1.sameClump(0, 3));
        assertEquals(2, c1.clumpSize(3));
        // Meld 3 and 4 into same clump.
        c1.meld(3, 4);
        assertTrue(c1.sameClump(0, 4));
        assertEquals(3, c1.clumpSize(3));
        // Meld 1 and 2 into same clump.
        c1.meld(1, 2);
        assertEquals(2, c1.clumpSize(1));
        assertEquals(2, c1.clumpSize(2));
        assertTrue(c1.sameClump(2, 1));
        assertFalse(c1.sameClump(2, 0));
        // Meld the two clumps into one big clump.
        c1.meld(2, 4);
        assertEquals(5, c1.clumpSize(1));
        assertEquals(5, c1.clumpSize(4));
    }

    @Test public void testClumpsFuzz() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int n = 2, trials = 22;
        int[] primes = {
            3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71
        };
        for(int i = 0; i < trials; i++) {
            Clumps c = new Clumps(n);
            int a = 0;
            int step = primes[rng.nextInt(primes.length)];
            // Keep melding randomly until everything becomes a single clump.
            while(c.clumpSize(0) < n) {
                int b = rng.nextInt(n);
                // Record the situation before the meld.
                int as = c.clumpSize(a);
                int bs = c.clumpSize(b);
                check.update(as);
                check.update(bs);
                if(c.sameClump(a, b)) {
                    assertEquals(as, bs);
                    // Clumping should do nothing if already in same clump.
                    assertFalse(c.meld(a, b));
                }
                else {
                    // After melding, two separate clumps become a new clump.
                    assertTrue(c.meld(a, b));
                    assertTrue(c.sameClump(a, b));
                    // New clump is as big as the two old clumps together.
                    assertEquals(as + bs, c.clumpSize(a));
                    assertEquals(as + bs, c.clumpSize(b));
                }
                // This discipline will visit each position exactly once thanks
                // to the guarantee that step and n have no common factors.
                a = (a + step) % n;
            }
            n = n * 2; // Next test is twice as big.
        }
        assertEquals(1485810445L, check.getValue());
    }    
}