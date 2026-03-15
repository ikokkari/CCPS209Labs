import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.zip.CRC32;
import java.util.Random;

public class ClumpsTest {

    // --- Basic operations ---

    @Test public void testClumpsExplicit() {
        Clumps c1 = new Clumps(5);
        assertEquals(1, c1.clumpSize(3));
        assertFalse(c1.sameClump(0, 3));
        c1.meld(0, 3);
        assertTrue(c1.sameClump(0, 3));
        assertEquals(2, c1.clumpSize(3));
        c1.meld(3, 4);
        assertTrue(c1.sameClump(0, 4));
        assertEquals(3, c1.clumpSize(3));
        c1.meld(1, 2);
        assertEquals(2, c1.clumpSize(1));
        assertEquals(2, c1.clumpSize(2));
        assertTrue(c1.sameClump(2, 1));
        assertFalse(c1.sameClump(2, 0));
        c1.meld(2, 4);
        assertEquals(5, c1.clumpSize(1));
        assertEquals(5, c1.clumpSize(4));
    }

    @Test public void testInitialState() {
        // All elements start as singleton clumps
        Clumps c = new Clumps(4);
        for (int i = 0; i < 4; i++) {
            assertEquals(1, c.clumpSize(i));
            assertTrue(c.sameClump(i, i)); // reflexive
        }
        // No two different elements share a clump
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                assertFalse(c.sameClump(i, j));
            }
        }
    }

    @Test public void testSingleElement() {
        Clumps c = new Clumps(1);
        assertEquals(1, c.clumpSize(0));
        assertTrue(c.sameClump(0, 0));
        // Melding with self does nothing
        assertFalse(c.meld(0, 0));
        assertEquals(1, c.clumpSize(0));
    }

    @Test public void testTwoElements() {
        Clumps c = new Clumps(2);
        assertFalse(c.sameClump(0, 1));
        assertTrue(c.meld(0, 1));
        assertTrue(c.sameClump(0, 1));
        assertEquals(2, c.clumpSize(0));
        assertEquals(2, c.clumpSize(1));
    }

    // --- Meld return value ---

    @Test public void testMeldReturnValue() {
        Clumps c = new Clumps(4);
        // Melding different clumps returns true
        assertTrue(c.meld(0, 1));
        // Melding already-same clump returns false
        assertFalse(c.meld(0, 1));
        assertFalse(c.meld(1, 0)); // order shouldn't matter
        // Melding an element with itself returns false
        assertFalse(c.meld(2, 2));
    }

    // --- Transitivity ---

    @Test public void testTransitivity() {
        // If a~b and b~c, then a~c after melding
        Clumps c = new Clumps(5);
        c.meld(0, 1);
        c.meld(1, 2);
        assertTrue(c.sameClump(0, 2)); // transitive through 1
        c.meld(3, 4);
        c.meld(2, 3);
        // Now all 5 should be in one clump
        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                assertTrue(c.sameClump(i, j));
            }
        }
        assertEquals(5, c.clumpSize(0));
    }

    // --- Size tracking ---

    @Test public void testClumpSizeAfterMelds() {
        Clumps c = new Clumps(8);
        // Create two clumps of size 2
        c.meld(0, 1);
        c.meld(2, 3);
        assertEquals(2, c.clumpSize(0));
        assertEquals(2, c.clumpSize(2));
        // Meld them into one clump of size 4
        c.meld(1, 3);
        assertEquals(4, c.clumpSize(0));
        assertEquals(4, c.clumpSize(1));
        assertEquals(4, c.clumpSize(2));
        assertEquals(4, c.clumpSize(3));
        // Others still singletons
        assertEquals(1, c.clumpSize(4));
    }

    @Test public void testMeldAllIntoOne() {
        // Meld all n elements into a single clump, one at a time
        int n = 10;
        Clumps c = new Clumps(n);
        for (int i = 1; i < n; i++) {
            assertTrue(c.meld(0, i));
        }
        for (int i = 0; i < n; i++) {
            assertEquals(n, c.clumpSize(i));
            assertTrue(c.sameClump(0, i));
        }
        // All further melds return false
        for (int i = 0; i < n; i++) {
            assertFalse(c.meld(i, (i + 1) % n));
        }
    }

    // --- Order independence ---

    @Test public void testMeldOrderDoesntMatter() {
        // Melding a,b should give same result as melding b,a
        Clumps c1 = new Clumps(4);
        Clumps c2 = new Clumps(4);
        c1.meld(0, 1); c1.meld(2, 3); c1.meld(0, 2);
        c2.meld(1, 0); c2.meld(3, 2); c2.meld(2, 0);
        // Both should have all 4 in one clump
        for (int i = 0; i < 4; i++) {
            assertEquals(4, c1.clumpSize(i));
            assertEquals(4, c2.clumpSize(i));
        }
    }

    // --- Melding two large clumps ---

    @Test public void testMeldTwoLargeClumps() {
        int n = 100;
        Clumps c = new Clumps(n);
        // Build two clumps: evens and odds
        for (int i = 2; i < n; i += 2) { c.meld(0, i); }
        for (int i = 3; i < n; i += 2) { c.meld(1, i); }
        assertEquals(50, c.clumpSize(0));
        assertEquals(50, c.clumpSize(1));
        assertFalse(c.sameClump(0, 1));
        // Meld them
        assertTrue(c.meld(0, 1));
        assertEquals(100, c.clumpSize(0));
        assertEquals(100, c.clumpSize(1));
        assertEquals(100, c.clumpSize(99));
    }

    // --- Path compression doesn't break anything ---

    @Test public void testPathCompressionConsistency() {
        // Build a long chain, then query from the tail to trigger path compression
        int n = 1000;
        Clumps c = new Clumps(n);
        // Chain: 0-1, 1-2, 2-3, ..., creating potentially deep trees
        for (int i = 0; i < n - 1; i++) {
            c.meld(i, i + 1);
        }
        assertEquals(n, c.clumpSize(0));
        // Query from various points to exercise path compression
        for (int i = 0; i < n; i++) {
            assertTrue(c.sameClump(0, i));
            assertEquals(n, c.clumpSize(i));
        }
        // After path compression, re-querying should still be consistent
        for (int i = n - 1; i >= 0; i--) {
            assertTrue(c.sameClump(i, 0));
        }
    }

    // --- Separate clumps stay separate ---

    @Test public void testDisjointClumpsStaySeparate() {
        Clumps c = new Clumps(9);
        // Three clumps: {0,1,2}, {3,4,5}, {6,7,8}
        c.meld(0, 1); c.meld(1, 2);
        c.meld(3, 4); c.meld(4, 5);
        c.meld(6, 7); c.meld(7, 8);
        // Within clump
        assertTrue(c.sameClump(0, 2));
        assertTrue(c.sameClump(3, 5));
        assertTrue(c.sameClump(6, 8));
        // Between clumps
        assertFalse(c.sameClump(0, 3));
        assertFalse(c.sameClump(3, 6));
        assertFalse(c.sameClump(0, 6));
        // Sizes
        assertEquals(3, c.clumpSize(0));
        assertEquals(3, c.clumpSize(4));
        assertEquals(3, c.clumpSize(8));
    }

    // --- CRC fuzz test ---

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
            while(c.clumpSize(0) < n) {
                int b = rng.nextInt(n);
                int as = c.clumpSize(a);
                int bs = c.clumpSize(b);
                check.update(as);
                check.update(bs);
                if(c.sameClump(a, b)) {
                    assertEquals(as, bs);
                    assertFalse(c.meld(a, b));
                }
                else {
                    assertTrue(c.meld(a, b));
                    assertTrue(c.sameClump(a, b));
                    assertEquals(as + bs, c.clumpSize(a));
                    assertEquals(as + bs, c.clumpSize(b));
                }
                a = (a + step) % n;
            }
            n = n * 2;
        }
        assertEquals(1485810445L, check.getValue());
    }
}