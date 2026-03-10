import org.junit.Test;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DistanceTestOne {

    private static final int SEED = 123456;

    // --- extractSquares tests ---

    @Test public void testExtractSquaresExplicit() {
        assertEquals(1, Distance.extractSquares(1));
        assertEquals(1, Distance.extractSquares(2));
        assertEquals(1, Distance.extractSquares(3));
        assertEquals(2, Distance.extractSquares(4));
        assertEquals(1, Distance.extractSquares(5));
        assertEquals(1, Distance.extractSquares(6));
        assertEquals(1, Distance.extractSquares(7));
        assertEquals(2, Distance.extractSquares(8));
        assertEquals(3, Distance.extractSquares(9));
        assertEquals(1, Distance.extractSquares(10));
        assertEquals(1, Distance.extractSquares(11));
        assertEquals(1, Distance.extractSquares(17));
    }

    @Test public void testExtractSquaresComposites() {
        assertEquals(6, Distance.extractSquares(2 * 2 * 3 * 3));
        assertEquals(2, Distance.extractSquares(2 * 2 * 3 * 5 * 7));
        assertEquals(7, Distance.extractSquares(2 * 7 * 7 * 11 * 13));
        assertEquals(5, Distance.extractSquares(2 * 5 * 5));
        assertEquals(19, Distance.extractSquares(2 * 5 * 11 * 19 * 19));
        assertEquals(2 * 39, Distance.extractSquares(2 * 2 * 2 * 5 * 39 * 39 * 109));
        assertEquals(7 * 7 * 13, Distance.extractSquares(2 * 7 * 7 * 7 * 7 * 11 * 13 * 13));
    }

    @Test public void testExtractSquaresPerfectSquares() {
        assertEquals(5 * 5 * 5, Distance.extractSquares(5 * 5 * 5 * 5 * 5 * 5));
        assertEquals(1000, Distance.extractSquares(1_000_000));
        assertEquals(12, Distance.extractSquares(144));
        assertEquals(100, Distance.extractSquares(10000));
    }

    @Test public void testExtractSquaresPrimes() {
        // All primes should return 1 (squarefree).
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 97, 101};
        for(int p : primes) {
            assertEquals("extractSquares(" + p + ")", 1, Distance.extractSquares(p));
        }
    }

    @Test public void testExtractSquaresMassTest() {
        CRC32 check = new CRC32();
        for(int n = 0; n < 100_000; n++) {
            int sp = Distance.extractSquares(n);
            assertEquals(0, n % ((long) sp * sp));
            int a = n / (sp * sp);
            assertEquals(n, sp * sp * a);
            check.update(sp);
        }
        assertEquals(4222950952L, check.getValue());
    }

    // --- toString tests ---

    @Test public void testToStringSingleTerm() {
        assertEquals("3Sqrt[61]", new Distance(3, 61).toString());
        assertEquals("0", new Distance(0, 5).toString());
        assertEquals("-42Sqrt[1003]", new Distance(-42, 1003).toString());
    }

    @Test public void testToStringConstant() {
        // Distance(a, 1) is an integer constant aSqrt[1] = a.
        assertEquals("1", new Distance(1, 1).toString());
        assertEquals("-5", new Distance(-5, 1).toString());
        assertEquals("0", new Distance(0, 1).toString());
        assertEquals("42", new Distance(42, 1).toString());
    }

    @Test public void testToStringCoefficientOne() {
        // Coefficient 1 is suppressed for non-constant terms.
        assertEquals("Sqrt[7]", new Distance(1, 7).toString());
        assertEquals("-Sqrt[7]", new Distance(-1, 7).toString());
    }

    @Test public void testToStringSquareExtraction() {
        // Distance(1, 4) -> extractSquares(4)=2, so 1*2=2, constant.
        assertEquals("2", new Distance(1, 4).toString());
        // Distance(3, 12) -> 12=4*3, extract=2, so 3*2*sqrt(3)=6sqrt(3).
        assertEquals("6Sqrt[3]", new Distance(3, 12).toString());
        // Distance(1, 50) -> 50=25*2, extract=5, so 5*sqrt(2).
        assertEquals("5Sqrt[2]", new Distance(1, 50).toString());
    }

    @Test public void testToStringMultiTerm() {
        TreeMap<Integer, Integer> c = new TreeMap<>();
        c.put(1, -1); c.put(2, 3); c.put(10, -1); c.put(17, 2);
        assertEquals("-1 + 3Sqrt[2] - Sqrt[10] + 2Sqrt[17]",
                new Distance(c).toString());
    }

    @Test public void testToStringAllNegative() {
        TreeMap<Integer, Integer> c = new TreeMap<>();
        c.put(2, -1); c.put(3, -1); c.put(5, -1);
        assertEquals("-Sqrt[2] - Sqrt[3] - Sqrt[5]", new Distance(c).toString());
    }

    @Test public void testToStringTermCombination() {
        // Powers of 2 all reduce to terms with base 1 or 2.
        TreeMap<Integer, Integer> c = new TreeMap<>();
        c.put(2, -1); c.put(4, -1); c.put(8, -1); c.put(16, -1);
        c.put(32, -1); c.put(64, -1); c.put(128, -1);
        assertEquals("-14 - 15Sqrt[2]", new Distance(c).toString());
    }

    @Test public void testToStringCompleteCancellation() {
        // All terms cancel out to zero.
        TreeMap<Integer, Integer> c = new TreeMap<>();
        c.put(11, 4); c.put(23, 4); c.put(44, -2); c.put(92, -2);
        assertEquals("0", new Distance(c).toString());
    }

    @Test public void testToStringReducesToConstant() {
        // All terms reduce to base 1, summing to a constant.
        TreeMap<Integer, Integer> c = new TreeMap<>();
        c.put(100, 5); c.put(10000, -5); c.put(1000000, 5);
        assertEquals("4550", new Distance(c).toString());
    }

    @Test public void testToStringFromSpec() {
        // Remaining cases from spec.
        TreeMap<Integer, Integer> c1 = new TreeMap<>();
        c1.put(99, 2); c1.put(999, 2); c1.put(9999, 2);
        assertEquals("6Sqrt[11] + 6Sqrt[111] + 6Sqrt[1111]",
                new Distance(c1).toString());

        TreeMap<Integer, Integer> c2 = new TreeMap<>();
        c2.put(5, 1); c2.put(10, 1); c2.put(15, 1);
        c2.put(20, 1); c2.put(25, 1); c2.put(30, 1);
        assertEquals("5 + 3Sqrt[5] + Sqrt[10] + Sqrt[15] + Sqrt[30]",
                new Distance(c2).toString());

        TreeMap<Integer, Integer> c3 = new TreeMap<>();
        c3.put(5, 1); c3.put(10, -1); c3.put(15, 1); c3.put(20, -1);
        assertEquals("-Sqrt[5] - Sqrt[10] + Sqrt[15]",
                new Distance(c3).toString());
    }

    // --- Constructor tests ---

    @Test public void testConstructorDefensiveCopy() {
        TreeMap<Integer, Integer> c = new TreeMap<>();
        c.put(2, 3); c.put(5, 7);
        Distance d = new Distance(c);
        // Mutate original map; Distance should be unaffected.
        c.put(2, 999);
        c.put(11, 42);
        assertEquals("3Sqrt[2] + 7Sqrt[5]", d.toString());
    }

    @Test public void testConstructorZeroCoefficientsIgnored() {
        TreeMap<Integer, Integer> c = new TreeMap<>();
        c.put(2, 0); c.put(3, 5); c.put(7, 0);
        Distance d = new Distance(c);
        assertEquals("5Sqrt[3]", d.toString());
    }

    @Test public void testConstructionMassTest() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10_000; i++) {
            int whole = rng.nextInt(3 * (i + 2));
            if(rng.nextBoolean()) { whole = -whole; }
            int base = rng.nextInt(3 * (i + 2)) + 1;
            Distance d = new Distance(whole, base);
            String rep = d.toString();
            try {
                check.update(rep.getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(4065287689L, check.getValue());
    }
}