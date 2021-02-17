import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

public class PolynomialTestThree {

    @Test public void testEquals() {
        int[] c1 = {-10, 99, 11, 12};
        int[] c2 = {-10, -99, 11, 12};
        Polynomial p1 = new Polynomial(c1);
        Polynomial p2 = new Polynomial(c2);
        Polynomial p3 = new Polynomial(c1);
        assertTrue(p1.equals(p1));
        assertTrue(p2.equals(p2));
        assertTrue(p1.equals(p3));
        assertTrue(p3.equals(p1));
        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));
        // The equals method must work between two objects of any type.
        assertFalse(p1.equals("hello world"));
        assertFalse(p1.equals(c1));
    }

    @Test public void testCompareTo() {
        // Remember to perform elementwise comparison down from the highest coefficient.
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
    private static final int TRIALS = 100000;

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
        TreeSet<Polynomial> tree = new TreeSet<Polynomial>();
        HashSet<Polynomial> hash = new HashSet<Polynomial>();
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
            // Comparing any polynomial to itself must return 0.
            assertEquals(0, p1.compareTo(p1));
            assertEquals(0, p2.compareTo(p2));
            // Order comparison must be symmetric.
            assertEquals(p1.compareTo(p2), -p2.compareTo(p1));
            check.update(p1.compareTo(p2));
        }
        // Both collections must now contain the same number of polynomials.
        // If they don't, the direction in which the failure happens provides
        // a clue about whether equals, compareTo and hashCode is at fault.
        assertFalse(tree.size() < hash.size());
        assertFalse(tree.size() > hash.size());

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
        assertEquals(28339163L, check.getValue());
    }
}