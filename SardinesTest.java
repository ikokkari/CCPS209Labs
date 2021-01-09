import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.*;
import java.util.zip.CRC32;

public class SardinesTest {

    @Test public void testExceptions() {
        try {
            
            try { // Negative number of elements.
                new Sardines(-10, 0);
                assertTrue(false); // this line should be unreachable
            } catch(IllegalArgumentException e) { }
            try { // Bits per element too big.
                new Sardines(12345, 1000);
                assertTrue(false);
            } catch(IllegalArgumentException e) { }
            try { // Negative bits per element.
                new Sardines(42, -42);
                assertTrue(false);
            } catch(IllegalArgumentException e) { }
            for(int k = 2; k < 30; k++) {
                try { // Total number of bits is a bit too much.
                    new Sardines(Integer.MAX_VALUE / k + 1, k);
                    assertTrue(false);
                } catch(IllegalArgumentException e) { }
            }
            
            // Successfully create a Sardines instance with 5 bits per element.
            Sardines ba = new Sardines(1000, 5);
            try { // Negative element value.
                ba.set(10, -1);
                assertTrue(false);
            } catch(IllegalArgumentException e) { }
            try { // Maximum legal element value, should succeed.
                ba.set(0, 31); // (1 << 5) == 32
                ba.set(999, 31); // Just in case there is a bug at the end case.
            } catch(IllegalArgumentException e) {
                System.out.println(e);
                assertTrue(false);
            }
            try { // Element value too big, should fail.
                ba.set(10, 32);
                assertTrue(false);
            } catch(IllegalArgumentException e) { }
            try { // Negative index, should fail.
                ba.set(-4, 17);
                assertTrue(false);
            } catch(ArrayIndexOutOfBoundsException e) { }
            try { // Index too large, should fail.
                ba.set(1000, 17);
                assertTrue(false);
            } catch(ArrayIndexOutOfBoundsException e) { }
            // If the execution gets this far, it's all hunky dunky.
        }
        catch(Exception e) {
            System.out.println("Caught " + e);
            assertTrue(false);
        }
    }
    
    @Test public void massTestThousandAndFive() {
        massTest(1000, 5);
    }
    
    @Test public void massTestMillionAndTwentyOne() {
        massTest(1_000_000, 21);
    }
    
    @Test public void massTestMillionAndThirtyOne() {
        massTest(1_000_000, 31);
    }
    
    // Two billion is just within the range of Java int type. Uncomment this
    // for a stress test of your Sardines implementation, if you feel brave
    // and your poor little laptop won't faint from the heat.
    /*
    @Test public void massTestBillionAndTwo() {
        massTest(1_000_000_000, 2);
    }
    */
    
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
        // Using the same rng, verify that all values are stored as they should.
        rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            assertEquals(rng.nextInt(range), ba.get(i));
        }
    }
    
}