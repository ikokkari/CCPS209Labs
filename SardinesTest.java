import org.junit.Test;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SardinesTest {

    // --- Constructor validation ---

    @Test public void testExceptions() {
        try {
            try { // Negative number of elements.
                new Sardines(-10, 0);
                fail();
            } catch(IllegalArgumentException ignored) { }
            try { // Bits per element too big.
                new Sardines(12345, 1000);
                fail();
            } catch(IllegalArgumentException ignored) { }
            try { // Negative bits per element.
                new Sardines(42, -42);
                fail();
            } catch(IllegalArgumentException ignored) { }
            try { // Zero bits per element.
                new Sardines(42, 0);
                fail();
            } catch(IllegalArgumentException ignored) { }
            for(int k = 2; k < 30; k++) {
                try { // Total number of bits overflows.
                    new Sardines(Integer.MAX_VALUE / k + 1, k);
                    fail();
                } catch(IllegalArgumentException ignored) { }
            }

            Sardines ba = new Sardines(1000, 5);
            try { // Negative element value.
                ba.set(10, -1);
                fail();
            } catch(IllegalArgumentException ignored) { }
            try { // Maximum legal value should succeed.
                ba.set(0, 31);
                ba.set(999, 31);
            } catch(IllegalArgumentException e) {
                fail();
            }
            try { // Value too big.
                ba.set(10, 32);
                fail();
            } catch(IllegalArgumentException ignored) { }
            try { // Negative index.
                ba.set(-4, 17);
                fail();
            } catch(ArrayIndexOutOfBoundsException ignored) { }
            try { // Index too large.
                ba.set(1000, 17);
                fail();
            } catch(ArrayIndexOutOfBoundsException ignored) { }
        }
        catch(Exception e) {
            System.out.println("Caught " + e);
            fail();
        }
    }

    // --- Basic get/set ---

    @Test public void testBasicGetSet() {
        Sardines s = new Sardines(5, 4); // 4 bits per element, max value 15
        // Initially all zeros
        for (int i = 0; i < 5; i++) {
            assertEquals(0, s.get(i));
        }
        // Set and get back
        s.set(0, 7);
        s.set(1, 0);
        s.set(2, 15);
        s.set(3, 1);
        s.set(4, 8);
        assertEquals(7, s.get(0));
        assertEquals(0, s.get(1));
        assertEquals(15, s.get(2));
        assertEquals(1, s.get(3));
        assertEquals(8, s.get(4));
    }

    @Test public void testOverwrite() {
        Sardines s = new Sardines(3, 8);
        s.set(1, 200);
        assertEquals(200, s.get(1));
        s.set(1, 42);
        assertEquals(42, s.get(1));
        s.set(1, 0);
        assertEquals(0, s.get(1));
    }

    // --- Bit width = 1 (boolean array) ---

    @Test public void testOneBitElements() {
        Sardines s = new Sardines(10, 1);
        for (int i = 0; i < 10; i++) {
            assertEquals(0, s.get(i));
        }
        s.set(3, 1);
        s.set(7, 1);
        assertEquals(0, s.get(0));
        assertEquals(1, s.get(3));
        assertEquals(0, s.get(4));
        assertEquals(1, s.get(7));
    }

    // --- Maximum bit width = 31 ---

    @Test public void testMaxBitWidth() {
        Sardines s = new Sardines(3, 31);
        int maxVal = Integer.MAX_VALUE; // 2^31 - 1
        s.set(0, maxVal);
        s.set(1, 0);
        s.set(2, 12345678);
        assertEquals(maxVal, s.get(0));
        assertEquals(0, s.get(1));
        assertEquals(12345678, s.get(2));
    }

    // --- Neighbour isolation: setting one element doesn't clobber neighbours ---

    @Test public void testNeighbourIsolation() {
        Sardines s = new Sardines(5, 3); // 3 bits, max value 7
        // Set all to known values
        s.set(0, 5); s.set(1, 3); s.set(2, 7); s.set(3, 0); s.set(4, 6);
        // Modify middle element
        s.set(2, 1);
        // Neighbours unchanged
        assertEquals(5, s.get(0));
        assertEquals(3, s.get(1));
        assertEquals(1, s.get(2));
        assertEquals(0, s.get(3));
        assertEquals(6, s.get(4));
    }

    @Test public void testNeighbourIsolationMaxBits() {
        // Particularly important for non-byte-aligned bit widths
        Sardines s = new Sardines(4, 7); // 7 bits, max 127
        s.set(0, 127); s.set(1, 0); s.set(2, 127); s.set(3, 0);
        // Verify pattern
        assertEquals(127, s.get(0));
        assertEquals(0, s.get(1));
        assertEquals(127, s.get(2));
        assertEquals(0, s.get(3));
        // Flip middle values
        s.set(1, 127); s.set(2, 0);
        assertEquals(127, s.get(0));
        assertEquals(127, s.get(1));
        assertEquals(0, s.get(2));
        assertEquals(0, s.get(3));
    }

    // --- Zero-length array ---

    @Test public void testZeroElements() {
        // Should construct without error
        Sardines s = new Sardines(0, 5);
        // No elements to get or set; just verify it doesn't crash
        try {
            s.get(0);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    // --- Boundary values ---

    @Test public void testBoundaryValues() {
        // For each bit width, test min (0), max (2^k - 1), and powers of 2
        for (int k = 1; k <= 16; k++) {
            int maxVal = (1 << k) - 1;
            Sardines s = new Sardines(3, k);
            s.set(0, 0);
            s.set(1, maxVal);
            s.set(2, 1 << (k - 1)); // highest single bit
            assertEquals(0, s.get(0));
            assertEquals(maxVal, s.get(1));
            assertEquals(1 << (k - 1), s.get(2));
        }
    }

    // --- All possible values for small k ---

    @Test public void testAllValuesSmallK() {
        // k=3: 8 possible values 0..7
        Sardines s = new Sardines(8, 3);
        for (int v = 0; v < 8; v++) {
            s.set(v, v);
        }
        for (int v = 0; v < 8; v++) {
            assertEquals(v, s.get(v));
        }
    }

    // --- Get exception on negative/out-of-range index ---

    @Test public void testGetExceptions() {
        Sardines s = new Sardines(10, 4);
        try { s.get(-1); fail(); }
        catch (ArrayIndexOutOfBoundsException ignored) {}
        try { s.get(10); fail(); }
        catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    // --- CRC mass tests ---

    @Test public void massTestThousandAndFive() {
        massTest(1000, 5);
    }

    @Test public void massTestMillionAndTwentyOne() {
        massTest(1_000_000, 21);
    }

    @Test public void massTestMillionAndThirtyOne() {
        massTest(1_000_000, 31);
    }

    private void massTest(int n, int k) {
        Random rng = new Random(12345);
        Sardines ba = new Sardines(n, k);
        int c = 0, cc, range = 1 << (k-1);
        // Each assigned element is correctly stored.
        for(int i = 0; i < 17 * n; i++) {
            ba.set(i % n, c);
            cc = ba.get(i % n);
            assertEquals(c, cc);
            c = (c + 1) % range;
        }
        // Make sure that assignment does not clobber neighbouring values.
        for(int i = 0; i < n; i++) {
            ba.set(i, rng.nextInt(range));
        }
        rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            assertEquals(rng.nextInt(range), ba.get(i));
        }
    }
}