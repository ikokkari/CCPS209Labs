import static org.junit.Assert.*;
import org.junit.Test;
import java.util.zip.CRC32;
import java.util.Random;

public class ClumpsTest {
    
    @Test public void testClumps() {
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
                    // Clumping should do nothing if already in same clump.
                    assertFalse(c.meld(a, b));
                    assertEquals(as, bs);
                }
                else {
                    // After melding, two separate clumps are the same clump.
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