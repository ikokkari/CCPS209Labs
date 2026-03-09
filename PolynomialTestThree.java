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

    private static final int SEED = 12345;

    private Polynomial createRandom(int deg, Random rng) {
        int[] c = new int[deg + 1];
        for(int j = 0; j < deg + 1; j++) {
            c[j] = rng.nextInt(20) - 10;
        }
        return new Polynomial(c);
    }

    // --- equals tests ---

    @Test public void testEqualsBasic() {
        Polynomial p1 = new Polynomial(new int[]{-10, 99, 11, 12});
        Polynomial p2 = new Polynomial(new int[]{-10, -99, 11, 12});
        Polynomial p3 = new Polynomial(new int[]{-10, 99, 11, 12});
        // Reflexive.
        assertEquals(p1, p1);
        // Symmetric.
        assertEquals(p1, p3);
        assertEquals(p3, p1);
        // Unequal polynomials.
        assertNotEquals(p1, p2);
        assertNotEquals(p2, p1);
    }

    @Test public void testEqualsWithNonPolynomial() {
        Polynomial p = new Polynomial(new int[]{-10, 99, 11, 12});
        assertNotEquals("hello world", p);
        assertNotEquals(p, "hello world");
        assertNotEquals(p, new int[]{-10, 99, 11, 12});
        assertNotEquals(p, null);
    }

    @Test public void testEqualsZeroPolynomial() {
        Polynomial z1 = new Polynomial(new int[]{0});
        Polynomial z2 = new Polynomial(new int[]{0, 0, 0, 0});
        assertEquals(z1, z2);
        assertEquals(z2, z1);
        // Zero is not equal to any nonzero constant.
        Polynomial c = new Polynomial(new int[]{1});
        assertNotEquals(z1, c);
    }

    @Test public void testEqualsWithTrailingZeros() {
        // Same polynomial, different trailing zeros in constructor input.
        Polynomial p1 = new Polynomial(new int[]{3, -5, 7});
        Polynomial p2 = new Polynomial(new int[]{3, -5, 7, 0, 0, 0});
        assertEquals(p1, p2);
        assertEquals(p2, p1);
    }

    @Test public void testEqualsDifferentDegrees() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{1, 2, 3, 4});
        assertNotEquals(p1, p2);
    }

    // --- hashCode tests ---

    @Test public void testHashCodeLeadingZeros() {
        Polynomial p1 = new Polynomial(new int[]{4, 2, -3});
        Polynomial p2 = new Polynomial(new int[]{4, 2, -3, 0});
        Polynomial p3 = new Polynomial(new int[]{4, 2, -3, 0, 0, 0, 0});
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(p2.hashCode(), p3.hashCode());
        assertEquals(p1, p2);
        assertEquals(p2, p3);
    }

    @Test public void testHashCodeConsistency() {
        // Same object returns same hash on repeated calls.
        Polynomial p = new Polynomial(new int[]{7, -3, 0, 2});
        int h1 = p.hashCode();
        int h2 = p.hashCode();
        assertEquals(h1, h2);
    }

    @Test public void testHashCodeZeroPolynomial() {
        // Zero polynomials from different-length arrays must hash the same.
        Polynomial z1 = new Polynomial(new int[]{0});
        Polynomial z2 = new Polynomial(new int[]{0, 0, 0});
        assertEquals(z1.hashCode(), z2.hashCode());
    }

    @Test public void testHashCodeEqualPolynomials() {
        // Equal polynomials constructed independently must have equal hashes.
        Polynomial p1 = new Polynomial(new int[]{5, -2, 0, 8});
        Polynomial p2 = new Polynomial(new int[]{5, -2, 0, 8});
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    // --- compareTo tests ---

    @Test public void testCompareToByDegree() {
        // Higher degree is always greater, regardless of coefficients.
        Polynomial p1 = new Polynomial(new int[]{-6, 99, 11, 12});     // degree 3
        Polynomial p2 = new Polynomial(new int[]{42, 10000000});        // degree 1
        assertEquals(+1, p1.compareTo(p2));
        assertEquals(-1, p2.compareTo(p1));
    }

    @Test public void testCompareToByHighestDifferingCoeff() {
        // Same degree, differ in lowest-order coefficient only.
        // Comparison must look from highest down, so the constant term decides last.
        Polynomial p1 = new Polynomial(new int[]{-6, 99, 11, 12});
        Polynomial p2 = new Polynomial(new int[]{6, -99, 11, 12});
        // Degrees equal (3). coeff[3]=12 equal, coeff[2]=11 equal,
        // coeff[1]: 99 > -99, so p1 > p2.
        assertEquals(+1, p1.compareTo(p2));
        assertEquals(-1, p2.compareTo(p1));
    }

    @Test public void testCompareToReflexive() {
        Polynomial p = new Polynomial(new int[]{3, -1, 7});
        assertEquals(0, p.compareTo(p));
    }

    @Test public void testCompareToEqualPolynomials() {
        Polynomial p1 = new Polynomial(new int[]{5, -2, 0, 8});
        Polynomial p2 = new Polynomial(new int[]{5, -2, 0, 8});
        assertEquals(0, p1.compareTo(p2));
        assertEquals(0, p2.compareTo(p1));
    }

    @Test public void testCompareToWithTrailingZeros() {
        Polynomial p1 = new Polynomial(new int[]{3, 2});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 0, 0});
        assertEquals(0, p1.compareTo(p2));
    }

    @Test public void testCompareToZeroVsConstants() {
        Polynomial zero = new Polynomial(new int[]{0});
        Polynomial pos = new Polynomial(new int[]{5});
        Polynomial neg = new Polynomial(new int[]{-3});
        assertEquals(-1, zero.compareTo(pos));
        assertEquals(+1, pos.compareTo(zero));
        assertEquals(+1, zero.compareTo(neg));
        assertEquals(-1, neg.compareTo(zero));
        assertEquals(+1, pos.compareTo(neg));
        assertEquals(-1, neg.compareTo(pos));
    }

    @Test public void testCompareToDifferOnlyInConstant() {
        Polynomial p1 = new Polynomial(new int[]{1, 0, 5});
        Polynomial p2 = new Polynomial(new int[]{2, 0, 5});
        // degree 2 equal, coeff[2]=5 equal, coeff[1]=0 equal, coeff[0]: 1 < 2.
        assertEquals(-1, p1.compareTo(p2));
        assertEquals(+1, p2.compareTo(p1));
    }

    @Test public void testCompareToDifferOnlyInInteriorTerm() {
        Polynomial p1 = new Polynomial(new int[]{1, 3, 5});
        Polynomial p2 = new Polynomial(new int[]{1, 4, 5});
        // degree 2 equal, coeff[2]=5 equal, coeff[1]: 3 < 4.
        assertEquals(-1, p1.compareTo(p2));
        assertEquals(+1, p2.compareTo(p1));
    }

    @Test public void testCompareToNegativeLeadingCoefficients() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, -1});
        Polynomial p2 = new Polynomial(new int[]{0, 0, 1});
        assertEquals(-1, p1.compareTo(p2));
        assertEquals(+1, p2.compareTo(p1));
    }

    // --- Mass / CRC tests ---

    @Test public void massTestHundredThousand() {
        massTest(100000, 28339163L);
    }

    @Test public void massTestMillion() {
        massTest(1000000, 3165052107L);
    }

    private void massTest(int trials, long expected) {
        Random rng = new Random(SEED);
        TreeSet<Polynomial> tree = new TreeSet<>();
        HashSet<Polynomial> hash = new HashSet<>();
        CRC32 check = new CRC32();
        for(int i = 0; i < trials; i++) {
            Polynomial p1 = createRandom(rng.nextInt(10), rng);
            Polynomial p2 = createRandom(rng.nextInt(10), rng);
            assertEquals(tree.contains(p1), hash.contains(p1));
            tree.add(p1);
            hash.add(p1);
            assertFalse(tree.size() < hash.size());
            assertFalse(tree.size() > hash.size());
            assertTrue(tree.contains(p1));
            assertTrue(hash.contains(p1));
            assertEquals(0, p1.compareTo(p1));
            assertEquals(0, p2.compareTo(p2));
            assertEquals(p1.compareTo(p2), -p2.compareTo(p1));
            check.update(p1.compareTo(p2));
        }
        for(Polynomial p: tree) {
            assertTrue(hash.contains(p));
        }
        for(Polynomial p: hash) {
            assertTrue(tree.contains(p));
        }
        assertEquals(expected, check.getValue());
    }
}