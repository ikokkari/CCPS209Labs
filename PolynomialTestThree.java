import org.junit.Test;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PolynomialTestThree {

    @Test public void testEquals() {
        int[] c1 = {-10, 99, 11, 12};
        int[] c2 = {-10, -99, 11, 12};
        Polynomial p1 = new Polynomial(c1);
        Polynomial p2 = new Polynomial(c2);
        Polynomial p3 = new Polynomial(c1);
        assertEquals(p1, p1);
        assertEquals(p2, p2);
        assertEquals(p1, p3);
        assertEquals(p3, p1);
        assertNotEquals(p1, p2);
        assertNotEquals(p2, p1);
        // The equals method must work between two objects of any type.
        assertNotEquals("hello world", p1);
        assertNotEquals(p1, c1);
    }

    @Test public void testHashCodeLeadingZeros() {
        // All these polynomials are equal, so they must have equal hash codes.
        int[] c1 = {4, 2, -3};
        int[] c2 = {4, 2, -3, 0};
        int[] c3 = {4, 2, -3, 0, 0, 0, 0};
        Polynomial p1 = new Polynomial(c1);
        Polynomial p2 = new Polynomial(c2);
        Polynomial p3 = new Polynomial(c3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(p2.hashCode(), p3.hashCode());
        assertEquals(p1, p2);
        assertEquals(p2, p3);
    }

    @Test public void testCompareTo() {
        // Remember to perform element-wise comparison down from the highest coefficient.
        int[] c1 = {-6, 99, 11, 12};
        int[] c2 = {6, -99, 11, 12};
        int[] c3 = {42, 10000000};
        Polynomial p1 = new Polynomial(c1);
        Polynomial p2 = new Polynomial(c2);
        Polynomial p3 = new Polynomial(c3);
        assertEquals(+1, p1.compareTo(p2));
        assertEquals(-1, p2.compareTo(p1));
        assertEquals(+1, p1.compareTo(p3));
        assertEquals(-1, p3.compareTo(p1));
        assertEquals(+1, p2.compareTo(p3));
        assertEquals(-1, p3.compareTo(p2));
        assertEquals(0, p1.compareTo(p1));
        assertEquals(0, p2.compareTo(p2));
        assertEquals(0, p3.compareTo(p3));
    }

    private static final int SEED = 12345;
    private static final int TRIALS = 1000000;

    private Polynomial createRandom(int deg, Random rng) {
        int[] c = new int[deg + 1];
        for(int j = 0; j < deg + 1; j++) {
            c[j] = rng.nextInt(20) - 10;
        }
        return new Polynomial(c);
    }

    @Test public void massTest() {
        Random rng = new Random(SEED);
        // These different collections are supposed to stay in lockstep so that
        // at all times, both tree and hash contain the exact same polynomials.
        TreeSet<Polynomial> tree = new TreeSet<>();
        HashSet<Polynomial> hash = new HashSet<>();
        CRC32 check = new CRC32();
        for(int i = 0; i < TRIALS; i++) {
            Polynomial p1 = createRandom(rng.nextInt(10), rng);
            Polynomial p2 = createRandom(rng.nextInt(10), rng);
            // Either the tree and the hash both already contain the
            // new random polynomial p1, or neither of them does.
            assertEquals(tree.contains(p1), hash.contains(p1));
            // Add that same polynomial to both collections in lockstep.
            tree.add(p1);
            hash.add(p1);
            // Both collections must contain the same number of polynomials. If
            // they don't, the direction in which the failure happens provides a
            // clue about whether equals, compareTo and hashCode is at fault. If
            // any of the following four assertions fails, uncomment next line to
            // see the polynomial p1 that caused the discrepancy.
            // System.out.println(p1);
            assertFalse(tree.size() < hash.size());
            assertFalse(tree.size() > hash.size());
            assertTrue(tree.contains(p1));
            assertTrue(hash.contains(p1));
            // Comparing any polynomial to itself must return 0.
            assertEquals(0, p1.compareTo(p1));
            assertEquals(0, p2.compareTo(p2));
            // Order comparison must be symmetric.
            assertEquals(p1.compareTo(p2), -p2.compareTo(p1));
            check.update(p1.compareTo(p2));
        }

        // The hash must now contain every polynomial that the tree contains.
        for(Polynomial p: tree) {
            assertTrue(hash.contains(p));
        }
        // The tree must now contain every polynomial that the hash contains.
        for(Polynomial p: hash) {
            assertTrue(tree.contains(p));
        }
        // So far, so good. Without this final hurdle using the checksum, all
        // previous tests could be trivially passed by defining your equals and
        // compareTo methods to simply consider all polynomials to be equal...
        assertEquals(3165052107L, check.getValue());
    }
}