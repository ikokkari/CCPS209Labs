import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J8Test {

    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger TEN = BigInteger.TEN;

    // ---------------------------------------------------------------
    // hittingIntegerPowers tests
    // ---------------------------------------------------------------

    @Test public void testHittingIntegerPowersExplicit() {
        int[] out = new int[2];

        // a is a power of b: 4 = 2^2, so 4^1 = 2^2 exactly
        P2J8.hittingIntegerPowers(2, 4, 100, out);
        assertArrayEquals(new int[]{2, 1}, out);

        // a^2 = b exactly: 6^2 = 36
        P2J8.hittingIntegerPowers(6, 36, 1000000, out);
        assertArrayEquals(new int[]{2, 1}, out);

        // Classic: 2^10 ~ 10^3 (1024 vs 1000)
        P2J8.hittingIntegerPowers(2, 10, 30, out);
        assertArrayEquals(new int[]{10, 3}, out);

        // 2^pa ~ 3^pb with t=100
        P2J8.hittingIntegerPowers(2, 3, 100, out);
        assertArrayEquals(new int[]{84, 53}, out);

        // 5^pa ~ 6^pb with t=1000
        P2J8.hittingIntegerPowers(5, 6, 1000, out);
        assertArrayEquals(new int[]{285, 256}, out);

        // 2^pa ~ 10^pb with t=100
        P2J8.hittingIntegerPowers(2, 10, 100, out);
        assertArrayEquals(new int[]{93, 28}, out);

        // Consecutive bases with tight tolerance
        P2J8.hittingIntegerPowers(2, 3, 10, out);
        verifyCloseEnough(2, out[0], 3, out[1], 10);

        // Larger bases
        P2J8.hittingIntegerPowers(7, 13, 100, out);
        verifyCloseEnough(7, out[0], 13, out[1], 100);
    }

    private void verifyCloseEnough(int a, int pa, int b, int pb, int t) {
        BigInteger aa = BigInteger.valueOf(a).pow(pa);
        BigInteger bb = BigInteger.valueOf(b).pow(pb);
        BigInteger diff = aa.subtract(bb).abs().multiply(BigInteger.valueOf(t));
        BigInteger smaller = aa.min(bb);
        assertTrue("Powers not close enough: " + a + "^" + pa + " vs " + b + "^" + pb,
                diff.compareTo(smaller) <= 0);
    }

    @Test public void testHittingIntegerPowersProperties() {
        // For every result, verify the "close enough" condition actually holds
        // Keep range moderate since BigInteger.pow is expensive for large exponents
        int[] out = new int[2];
        int[] tolerances = {10, 100, 1000};
        for (int b = 3; b < 10; b++) {
            for (int a = 2; a < b; a++) {
                for (int t : tolerances) {
                    P2J8.hittingIntegerPowers(a, b, t, out);
                    assertTrue("pa must be positive", out[0] >= 1);
                    assertTrue("pb must be positive", out[1] >= 1);
                    verifyCloseEnough(a, out[0], b, out[1], t);
                }
            }
        }
    }

    @Test public void testHittingIntegerPowersFuzz() {
        CRC32 check = new CRC32();
        int[] out = new int[2];
        int[] tens = {1, 10, 100, 10000, 10000, 100000};
        for (int b = 3; b < 20; b++) {
            for (int a = 2; a < b; a++) {
                int t = 2 + (a + b) % 3;
                P2J8.hittingIntegerPowers(a, b, tens[t], out);
                check.update(out[0]);
                check.update(out[1]);
            }
        }
        assertEquals(3805180419L, check.getValue());
    }

    // ---------------------------------------------------------------
    // nearestPolygonalNumber tests
    // ---------------------------------------------------------------

    private static BigInteger pol(BigInteger i, int s) {
        BigInteger ss = BigInteger.valueOf(s);
        return ss.subtract(TWO).multiply(i).multiply(i.subtract(BigInteger.ONE))
                .divide(TWO).add(i);
    }

    @Test public void testNearestPolygonalNumberExplicit() {
        // n = 1: first polygonal number for any s is 1
        assertEquals(BigInteger.ONE, P2J8.nearestPolygonalNumber(BigInteger.ONE, 3));
        assertEquals(BigInteger.ONE, P2J8.nearestPolygonalNumber(BigInteger.ONE, 8));

        // n is exactly a polygonal number
        assertEquals(new BigInteger("45"),
                P2J8.nearestPolygonalNumber(new BigInteger("45"), 3));

        // n between two triangular numbers: 7 between 6 and 10, closer to 6
        assertEquals(new BigInteger("6"),
                P2J8.nearestPolygonalNumber(new BigInteger("7"), 3));

        // 98 between triangular 91 and 105, closer to 91
        assertEquals(new BigInteger("91"),
                P2J8.nearestPolygonalNumber(new BigInteger("98"), 3));

        // Pentagonal cases from original
        assertEquals(new BigInteger("35"),
                P2J8.nearestPolygonalNumber(new BigInteger("42"), 5));
        assertEquals(new BigInteger("117"),
                P2J8.nearestPolygonalNumber(new BigInteger("131"), 5));
        assertEquals(new BigInteger("4030"),
                P2J8.nearestPolygonalNumber(new BigInteger("3999"), 5));

        // Octagonal: n=50 between 40 and 65, closer to 40
        assertEquals(new BigInteger("40"),
                P2J8.nearestPolygonalNumber(new BigInteger("50"), 8));

        // Halfway tiebreak: return smaller
        // Triangular: midpoint of 1 and 3 is 2, tie -> return 1
        assertEquals(BigInteger.ONE,
                P2J8.nearestPolygonalNumber(TWO, 3));
        // Pentagonal: 117 and 145, midpoint = 131, tie -> return 117
        assertEquals(new BigInteger("117"),
                P2J8.nearestPolygonalNumber(new BigInteger("131"), 5));
    }

    @Test public void testNearestPolygonalNumberIsPolygonal() {
        // For a range of inputs, verify the result is an s-gonal number
        // and is the nearest one
        Random rng = new Random(42);
        for (int trial = 0; trial < 200; trial++) {
            int s = rng.nextInt(20) + 3;
            BigInteger n = BigInteger.valueOf(rng.nextInt(10000) + 1);
            BigInteger result = P2J8.nearestPolygonalNumber(n, s);

            // Find which s-gonal number this is
            BigInteger i = BigInteger.ONE;
            while (pol(i, s).compareTo(result) < 0) {
                i = i.add(BigInteger.ONE);
            }
            assertEquals("Not an s-gonal number (s=" + s + ", n=" + n + ")",
                    result, pol(i, s));

            // Check neighbors to verify it's nearest
            BigInteger distResult = n.subtract(result).abs();
            if (i.compareTo(BigInteger.ONE) > 0) {
                BigInteger prev = pol(i.subtract(BigInteger.ONE), s);
                BigInteger distPrev = n.subtract(prev).abs();
                assertTrue("Prev is closer for s=" + s + ", n=" + n,
                        distResult.compareTo(distPrev) <= 0);
            }
            BigInteger next = pol(i.add(BigInteger.ONE), s);
            BigInteger distNext = n.subtract(next).abs();
            assertTrue("Next is closer for s=" + s + ", n=" + n,
                    distResult.compareTo(distNext) < 0 ||
                            (distResult.equals(distNext) && result.compareTo(next) < 0));
        }
    }

    @Test public void testNearestPolygonalNumberAllSides() {
        // Test across different s values for a fixed n
        BigInteger n = new BigInteger("100");
        for (int s = 3; s <= 30; s++) {
            BigInteger result = P2J8.nearestPolygonalNumber(n, s);
            assertTrue("Result must be positive for s=" + s,
                    result.compareTo(BigInteger.ZERO) > 0);
            // Verify it's an actual s-gonal number
            BigInteger i = BigInteger.ONE;
            while (pol(i, s).compareTo(result) < 0) {
                i = i.add(BigInteger.ONE);
            }
            assertEquals("Not s-gonal for s=" + s, result, pol(i, s));
        }
    }

    @Test public void testNearestPolygonalNumberFuzz() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        BigInteger curr = BigInteger.ONE;
        int[] tens = {1, 10, 100, 1000, 10000};
        for (int i = 0; i < 1000; i++) {
            if (i % 5 == 0) { curr = curr.multiply(TWO); }
            int s = rng.nextInt(100) + 2;
            BigInteger result = P2J8.nearestPolygonalNumber(curr, s);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ignored) { }
            curr = curr.add(new BigInteger("" + rng.nextInt(100) * tens[(i / 5) % tens.length]));
        }
        assertEquals(3138704967L, check.getValue());
    }
}