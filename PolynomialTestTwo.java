import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolynomialTestTwo {

    private static final int SEED = 12345;

    private Polynomial createRandom(int deg, Random rng) {
        int[] c = new int[deg + 1];
        for(int j = 0; j < deg + 1; j++) {
            c[j] = rng.nextInt(20) - 10;
        }
        return new Polynomial(c);
    }

    private boolean polyEq(Polynomial p1, Polynomial p2, CRC32 check) {
        if(p1.getDegree() != p2.getDegree()) { return false; }
        for(int k = 0; k <= p1.getDegree(); k++) {
            if(check != null) { check.update(p1.getCoefficient(k)); }
            if(p1.getCoefficient(k) != p2.getCoefficient(k)) { return false; }
        }
        return true;
    }

    // --- Add: explicit tests ---

    @Test public void testAddZeroPlusPolynomial() {
        // Adding zero to a polynomial should return that polynomial.
        Polynomial zero = new Polynomial(new int[]{0});
        Polynomial p = new Polynomial(new int[]{-42, 99, 17, 101});
        Polynomial r1 = zero.add(p);
        Polynomial r2 = p.add(zero);
        assertTrue(polyEq(r1, p, null));
        assertTrue(polyEq(r2, p, null));
    }

    @Test public void testAddHighestTermsCancelOut() {
        // Highest terms cancel, reducing the degree.
        Polynomial p1 = new Polynomial(new int[]{5, -5, 2, -2, 4});
        Polynomial p2 = new Polynomial(new int[]{3, 5, -2, 2, -4});
        Polynomial r = p1.add(p2);
        Polynomial expected = new Polynomial(new int[]{8});
        assertTrue(polyEq(r, expected, null));
    }

    @Test public void testAddDifferentDegrees() {
        Polynomial p1 = new Polynomial(new int[]{-3, 9, -2, 0, 0, 4});
        Polynomial p2 = new Polynomial(new int[]{5, -7, 0, 1, 0, 0, 5});
        Polynomial r = p1.add(p2);
        Polynomial expected = new Polynomial(new int[]{2, 2, -2, 1, 0, 4, 5});
        assertTrue(polyEq(r, expected, null));
    }

    @Test public void testAddHighDegreeWithLow() {
        Polynomial p1 = new Polynomial(new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12345});
        Polynomial p2 = new Polynomial(new int[]{-9, 1, 2, 3, 4, 5, 6});
        Polynomial r = p1.add(p2);
        Polynomial expected = new Polynomial(new int[]{-10, 1, 2, 3, 4, 5, 6, 0, 0, 0, 12345});
        assertTrue(polyEq(r, expected, null));
    }

    @Test public void testAddPolynomialToOwnNegation() {
        // p + (-p) should yield the zero polynomial.
        Polynomial p = new Polynomial(new int[]{3, -5, 0, 7});
        Polynomial neg = new Polynomial(new int[]{-3, 5, 0, -7});
        Polynomial r = p.add(neg);
        assertEquals(0, r.getDegree());
        assertEquals(0, r.getCoefficient(0));
    }

    @Test public void testAddCommutativity() {
        Polynomial p1 = new Polynomial(new int[]{4, -1, 3});
        Polynomial p2 = new Polynomial(new int[]{-2, 7, 0, 5});
        assertTrue(polyEq(p1.add(p2), p2.add(p1), null));
    }

    @Test public void testAddDoesNotMutateOperands() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{4, 5, 6});
        p1.add(p2);
        // Verify originals unchanged.
        assertEquals(2, p1.getDegree());
        assertEquals(1, p1.getCoefficient(0));
        assertEquals(3, p1.getCoefficient(2));
        assertEquals(2, p2.getDegree());
        assertEquals(4, p2.getCoefficient(0));
        assertEquals(6, p2.getCoefficient(2));
    }

    // --- Multiply: explicit tests ---

    @Test public void testMultiplyBasic() {
        // (3x^2 - 5x + 7) * (-4x^3 + 6) = -12x^5 + 20x^4 - 28x^3 + 18x^2 - 30x + 42
        Polynomial p1 = new Polynomial(new int[]{7, -5, 3});
        Polynomial p2 = new Polynomial(new int[]{6, 0, 0, -4});
        Polynomial r = p1.multiply(p2);
        Polynomial expected = new Polynomial(new int[]{42, -30, 18, -28, 20, -12});
        assertTrue(polyEq(r, expected, null));
        // Commutativity.
        assertTrue(polyEq(r, p2.multiply(p1), null));
    }

    @Test public void testMultiplySparse() {
        // (x^10 - 2x^5 + x) * (-4x^2 + 2x + 1)
        Polynomial p1 = new Polynomial(new int[]{0, 1, 0, 0, 0, -2, 0, 0, 0, 0, 1});
        Polynomial p2 = new Polynomial(new int[]{1, 2, -4});
        Polynomial r = p1.multiply(p2);
        Polynomial expected = new Polynomial(new int[]{0, 1, 2, -4, 0, -2, -4, 8, 0, 0, 1, 2, -4});
        assertTrue(polyEq(r, expected, null));
        assertTrue(polyEq(r, p2.multiply(p1), null));
    }

    @Test public void testMultiplyByZero() {
        // Anything times zero is zero.
        Polynomial p = new Polynomial(new int[]{3, -5, 0, 7});
        Polynomial zero = new Polynomial(new int[]{0});
        Polynomial r1 = p.multiply(zero);
        Polynomial r2 = zero.multiply(p);
        assertEquals(0, r1.getDegree());
        assertEquals(0, r1.getCoefficient(0));
        assertEquals(0, r2.getDegree());
        assertEquals(0, r2.getCoefficient(0));
    }

    @Test public void testMultiplyByOne() {
        // Multiplicative identity: p * 1 = p.
        Polynomial p = new Polynomial(new int[]{3, -5, 0, 7});
        Polynomial one = new Polynomial(new int[]{1});
        assertTrue(polyEq(p, p.multiply(one), null));
        assertTrue(polyEq(p, one.multiply(p), null));
    }

    @Test public void testMultiplyByConstant() {
        // p * {2} doubles all coefficients.
        Polynomial p = new Polynomial(new int[]{3, -5, 7});
        Polynomial two = new Polynomial(new int[]{2});
        Polynomial r = p.multiply(two);
        Polynomial expected = new Polynomial(new int[]{6, -10, 14});
        assertTrue(polyEq(r, expected, null));
    }

    @Test public void testMultiplyByX() {
        // Multiplying by x = {0, 1} shifts all coefficients up by one.
        Polynomial p = new Polynomial(new int[]{3, -5, 7});
        Polynomial x = new Polynomial(new int[]{0, 1});
        Polynomial r = p.multiply(x);
        Polynomial expected = new Polynomial(new int[]{0, 3, -5, 7});
        assertTrue(polyEq(r, expected, null));
    }

    @Test public void testMultiplyDifferenceOfSquares() {
        // (x - 1)(x + 1) = x^2 - 1.
        Polynomial xm1 = new Polynomial(new int[]{-1, 1});
        Polynomial xp1 = new Polynomial(new int[]{1, 1});
        Polynomial r = xm1.multiply(xp1);
        Polynomial expected = new Polynomial(new int[]{-1, 0, 1});
        assertTrue(polyEq(r, expected, null));
    }

    @Test public void testMultiplyPerfectSquare() {
        // (x + 1)^2 = x^2 + 2x + 1.
        Polynomial xp1 = new Polynomial(new int[]{1, 1});
        Polynomial r = xp1.multiply(xp1);
        Polynomial expected = new Polynomial(new int[]{1, 2, 1});
        assertTrue(polyEq(r, expected, null));
    }

    @Test public void testMultiplyDoesNotMutateOperands() {
        Polynomial p1 = new Polynomial(new int[]{1, 2});
        Polynomial p2 = new Polynomial(new int[]{3, 4});
        p1.multiply(p2);
        assertEquals(1, p1.getDegree());
        assertEquals(1, p1.getCoefficient(0));
        assertEquals(2, p1.getCoefficient(1));
        assertEquals(1, p2.getDegree());
        assertEquals(3, p2.getCoefficient(0));
        assertEquals(4, p2.getCoefficient(1));
    }

    // --- Mass / CRC tests ---

    @Test public void massTestTenThousand() {
        massTest(10000, 2342773557L);
    }

    @Test public void massTestHundredThousand() {
        massTest(100000, 529848787L);
    }

    private void massTest(int trials, long expected) {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < trials; i++) {
            Polynomial p1 = createRandom(rng.nextInt(10 + i / 1000), rng);
            Polynomial p2 = createRandom(rng.nextInt(10 + i / 1000), rng);
            Polynomial p3 = p1.add(p2);
            Polynomial p4 = p2.add(p1);
            assertTrue(polyEq(p3, p4, check));
            Polynomial p5 = p1.multiply(p2);
            Polynomial p6 = p2.multiply(p1);
            assertTrue(polyEq(p5, p6, check));
        }
        assertEquals(expected, check.getValue());
    }
}