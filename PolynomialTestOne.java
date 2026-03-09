import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class PolynomialTestOne {

    // --- Explicit tests ---

    @Test public void testConstructorAndDegree() {
        // Standard polynomial: 5 - 2x^2 + 3x^3
        Polynomial p = new Polynomial(new int[]{5, 0, -2, 3});
        assertEquals(3, p.getDegree());

        // Trailing zeros should be trimmed: {99, 0, 0, 0, 0} -> degree 0
        Polynomial p2 = new Polynomial(new int[]{99, 0, 0, 0, 0, 0, 0, 0});
        assertEquals(0, p2.getDegree());

        // Zero polynomial from all-zero array.
        Polynomial p3 = new Polynomial(new int[]{0, 0, 0, 0});
        assertEquals(0, p3.getDegree());

        // Single element array.
        Polynomial p4 = new Polynomial(new int[]{42});
        assertEquals(0, p4.getDegree());

        // Leading zeros trimmed to reveal degree 4: 1 + 5x^4
        Polynomial p5 = new Polynomial(new int[]{1, 0, 0, 0, 5, 0, 0});
        assertEquals(4, p5.getDegree());
    }

    @Test public void testDefensiveCopy() {
        // Modifying the original array must not affect the Polynomial.
        int[] c = {5, 0, -2, 3};
        Polynomial p = new Polynomial(c);
        c[3] = 9999;
        assertEquals(3, p.getCoefficient(3));
        c[0] = -1;
        assertEquals(5, p.getCoefficient(0));
    }

    @Test public void testGetCoefficient() {
        // 42 - 7x + 5x^3
        Polynomial p = new Polynomial(new int[]{42, -7, 0, 5});
        assertEquals(42, p.getCoefficient(0));
        assertEquals(-7, p.getCoefficient(1));
        assertEquals(0, p.getCoefficient(2));
        assertEquals(5, p.getCoefficient(3));

        // Beyond degree returns 0.
        assertEquals(0, p.getCoefficient(4));
        assertEquals(0, p.getCoefficient(100));
        assertEquals(0, p.getCoefficient(12345));

        // Negative index returns 0.
        assertEquals(0, p.getCoefficient(-1));
        assertEquals(0, p.getCoefficient(-100));
    }

    @Test public void testGetCoefficientZeroPoly() {
        Polynomial zero = new Polynomial(new int[]{0});
        assertEquals(0, zero.getCoefficient(0));
        assertEquals(0, zero.getCoefficient(1));
        assertEquals(0, zero.getCoefficient(-1));
    }

    @Test public void testEvaluateBasic() {
        // 5 - 2x^2 + 3x^3
        Polynomial p = new Polynomial(new int[]{5, 0, -2, 3});
        assertEquals(5, p.evaluate(0));
        assertEquals(6, p.evaluate(1));
        assertEquals(21, p.evaluate(2));
        assertEquals(-27, p.evaluate(-2)); // 5 + 0 - 8 - 24
    }

    @Test public void testEvaluateZeroPoly() {
        Polynomial zero = new Polynomial(new int[]{0, 0, 0});
        assertEquals(0, zero.evaluate(0));
        assertEquals(0, zero.evaluate(5));
        assertEquals(0, zero.evaluate(-3));
        assertEquals(0, zero.evaluate(1000));
    }

    @Test public void testEvaluateConstant() {
        Polynomial c = new Polynomial(new int[]{-7});
        assertEquals(-7, c.evaluate(0));
        assertEquals(-7, c.evaluate(100));
        assertEquals(-7, c.evaluate(-999));
    }

    @Test public void testEvaluateLinear() {
        // 3 - 2x
        Polynomial p = new Polynomial(new int[]{3, -2});
        assertEquals(3, p.evaluate(0));
        assertEquals(1, p.evaluate(1));
        assertEquals(-7, p.evaluate(5));
        assertEquals(9, p.evaluate(-3));
    }

    @Test public void testEvaluateLargerValues() {
        // x^2: evaluate at larger values, result fits in long.
        Polynomial p = new Polynomial(new int[]{0, 0, 1});
        assertEquals(0L, p.evaluate(0));
        assertEquals(1L, p.evaluate(1));
        assertEquals(10000000000L, p.evaluate(100000));

        // 1 + x: evaluate at large x.
        Polynomial q = new Polynomial(new int[]{1, 1});
        assertEquals(1000001L, q.evaluate(1000000));
    }

    @Test public void testEvaluateNegativeX() {
        // x^3 - x: odd function
        Polynomial p = new Polynomial(new int[]{0, -1, 0, 1});
        assertEquals(0, p.evaluate(0));
        assertEquals(0, p.evaluate(1));
        assertEquals(0, p.evaluate(-1));
        assertEquals(6, p.evaluate(2));   // 8 - 2
        assertEquals(-6, p.evaluate(-2)); // -8 + 2
    }

    @Test public void testDegreeAfterTrailingZeroTrim() {
        // {0, 0, 0, 0, 0, 3, 0, 0} -> degree 5
        Polynomial p = new Polynomial(new int[]{0, 0, 0, 0, 0, 3, 0, 0});
        assertEquals(5, p.getDegree());
        assertEquals(3, p.getCoefficient(5));
        assertEquals(0, p.getCoefficient(6));
        assertEquals(0, p.getCoefficient(7));
    }

    // --- Mass / CRC tests ---

    private static final int SEED = 12345;

    @Test public void massTestHundredThousand() {
        massTest(100000, 2488587162L);
    }

    @Test public void massTestMillion() {
        massTest(1000000, 427606002L);
    }

    private void massTest(int trials, long expected) {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < trials; i++) {
            int deg = rng.nextInt(10);
            int[] c = new int[deg + 1];
            for(int j = 0; j < deg + 1; j++) {
                c[j] = rng.nextInt(20) - 10;
            }
            Polynomial p = new Polynomial(c);
            check.update(p.getDegree());
            for(int j = -5; j <= 5; j++) {
                check.update((int) p.evaluate(j));
            }
        }
        assertEquals(expected, check.getValue());
    }
}