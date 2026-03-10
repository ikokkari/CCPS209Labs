import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class DistanceTestTwo {

    public static final Distance ZERO = new Distance(0, 1);
    public static final Distance ONE = new Distance(1, 1);
    private static final int SEED = 123456;

    // --- Addition / Subtraction explicit tests ---

    @Test public void testAddSubtractExplicit() {
        Map<Integer, Integer> coeff1 = Map.of(5, 2, 10, 3);
        Distance d1 = new Distance(coeff1);

        Map<Integer, Integer> coeff2 = Map.of(3, -1, 7, 2, 10, -3);
        Distance d2 = new Distance(coeff2);

        Distance d3 = d1.add(d2);
        assertEquals("-Sqrt[3] + 2Sqrt[5] + 2Sqrt[7]", d3.toString());

        Distance d4 = d1.subtract(d2);
        assertEquals("Sqrt[3] + 2Sqrt[5] - 2Sqrt[7] + 6Sqrt[10]", d4.toString());

        // Addition should be commutative.
        assertEquals(d1.add(d2).toString(), d2.add(d1).toString());
        assertEquals(d1.add(d3).toString(), d3.add(d1).toString());
        assertEquals(d2.add(d4).toString(), d4.add(d2).toString());
    }

    @Test public void testAddWithZero() {
        Distance d = new Distance(Map.of(5, 2, 10, 3));
        assertEquals(d.toString(), d.add(ZERO).toString());
        assertEquals(d.toString(), ZERO.add(d).toString());
    }

    @Test public void testSubtractFromSelf() {
        Distance d = new Distance(Map.of(3, 7, 11, -4, 1, 5));
        assertEquals("0", d.subtract(d).toString());
    }

    @Test public void testAddThenSubtractRoundtrip() {
        Distance d1 = new Distance(Map.of(2, 3, 7, -1));
        Distance d2 = new Distance(Map.of(5, 2, 13, 4));
        assertEquals(d1.toString(), d1.add(d2).subtract(d2).toString());
        assertEquals(d2.toString(), d2.add(d1).subtract(d1).toString());
    }

    @Test public void testAddDoesNotMutateOperands() {
        Distance d1 = new Distance(3, 5);
        Distance d2 = new Distance(7, 11);
        String s1 = d1.toString();
        String s2 = d2.toString();
        d1.add(d2);
        d1.subtract(d2);
        assertEquals(s1, d1.toString());
        assertEquals(s2, d2.toString());
    }

    // --- Multiplication explicit tests ---

    @Test public void testMultiplyExplicit() {
        Map<Integer, Integer> coeff1 = Map.of(5, 2, 10, 3);
        Distance d1 = new Distance(coeff1);

        Map<Integer, Integer> coeff2 = Map.of(3, -1, 7, 2, 10, -3);
        Distance d2 = new Distance(coeff2);

        Distance d3 = d1.multiply(d2);
        assertEquals("-90 - 30Sqrt[2] - 2Sqrt[15] - 3Sqrt[30] + 4Sqrt[35] + 6Sqrt[70]",
                d3.toString());

        Distance d4 = d1.multiply(d1);
        assertEquals("110 + 60Sqrt[2]", d4.toString());

        Map<Integer, Integer> coeff5 = Map.of(20, -1, 70, 2);
        Distance d5 = new Distance(coeff5);

        Distance d6 = d1.multiply(d5);
        assertEquals("-20 - 30Sqrt[2] + 60Sqrt[7] + 20Sqrt[14]", d6.toString());
    }

    @Test public void testMultiplyConjugatesCancelRoots() {
        // (2Sqrt[3] + 3Sqrt[2]) * (-2Sqrt[3] + 3Sqrt[2]) = 18 - 12 = 6
        Distance d7 = new Distance(Map.of(3, 2, 2, 3));
        Distance d8 = new Distance(Map.of(3, -2, 2, 3));
        assertEquals("6", d7.multiply(d8).toString());
    }

    @Test public void testMultiplyFourTerms() {
        Distance d10 = new Distance(Map.of(30, -1, 10, 1, 5, -1, 15, -1));
        Distance d11 = new Distance(Map.of(30, 1, 10, 1, 5, -1, 15, 1));
        assertEquals("-30 - 40Sqrt[2]", d10.multiply(d11).toString());
    }

    @Test public void testMultiplyByZero() {
        Distance d = new Distance(Map.of(2, 5, 7, -3));
        assertEquals("0", d.multiply(ZERO).toString());
        assertEquals("0", ZERO.multiply(d).toString());
    }

    @Test public void testMultiplyByOne() {
        Distance d = new Distance(Map.of(2, 5, 7, -3));
        assertEquals(d.toString(), d.multiply(ONE).toString());
        assertEquals(d.toString(), ONE.multiply(d).toString());
    }

    @Test public void testMultiplyByNegativeOne() {
        Distance d = new Distance(Map.of(2, 3, 5, -1));
        Distance neg = new Distance(-1, 1);
        Distance result = d.multiply(neg);
        assertEquals("0", d.add(result).toString());
    }

    @Test public void testMultiplyCommutativity() {
        Distance d1 = new Distance(Map.of(5, 2, 10, 3));
        Distance d2 = new Distance(Map.of(3, -1, 7, 2, 10, -3));
        Distance d5 = new Distance(Map.of(20, -1, 70, 2));
        Distance d8 = new Distance(Map.of(3, -2, 2, 3));

        assertEquals(d1.multiply(d2).toString(), d2.multiply(d1).toString());
        assertEquals(d1.multiply(d5).toString(), d5.multiply(d1).toString());
        assertEquals(d5.multiply(d8).toString(), d8.multiply(d5).toString());
    }

    @Test public void testDistributiveProperty() {
        // d1 * (d2 + d3) = d1*d2 + d1*d3
        Distance d1 = new Distance(Map.of(5, 2, 10, 3));
        Distance d2 = new Distance(Map.of(3, 2, 2, 3));
        Distance d3 = new Distance(Map.of(3, -2, 2, 3));
        Distance lhs = d1.multiply(d2.add(d3));
        Distance rhs = d1.multiply(d2).add(d1.multiply(d3));
        assertEquals(lhs.toString(), rhs.toString());
    }

    @Test public void testMultiplyDoesNotMutateOperands() {
        Distance d1 = new Distance(3, 5);
        Distance d2 = new Distance(7, 11);
        String s1 = d1.toString();
        String s2 = d2.toString();
        d1.multiply(d2);
        assertEquals(s1, d1.toString());
        assertEquals(s2, d2.toString());
    }

    // --- Mass / CRC tests ---

    @Test public void testAdd() {
        testArithmetic(true, 2784019965L);
    }

    @Test public void testSubtract() {
        testArithmetic(false, 1739788852L);
    }

    private void testArithmetic(boolean add, long expected) {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int N = 10_000;
        Distance[] ds = new Distance[3 * N];
        for(int i = 0; i < ds.length; i++) {
            if(i < N) {
                int whole = rng.nextInt(i + 3);
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 1;
                ds[i] = new Distance(whole, base);
            }
            else {
                int j1 = rng.nextInt(i);
                int j2 = rng.nextInt(i);
                ds[i] = add ? ds[j1].add(ds[j2]) : ds[j1].subtract(ds[j2]);
                Distance sub = ds[i].subtract(ds[i]);
                assertEquals(ZERO.toString(), sub.toString());
                String si = ds[i].toString();
                String sii = ds[i].add(ds[i - 1]).subtract(ds[i - 1]).toString();
                assertEquals(si, sii);
                try {
                    check.update(ds[i].toString().getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) {}
            }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testMultiply() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int N = 50;
        Distance[] ds = new Distance[20 * N];
        for(int i = 0; i < ds.length; i++) {
            if(i < N) {
                int whole = rng.nextInt(2 + i / 5);
                int base = rng.nextInt(4 * (i + 1)) + 1;
                ds[i] = new Distance(whole, base);
                ds[i + 1] = new Distance(-whole, base);
                i++;
            }
            else if(i < 10 * N) {
                int j1 = rng.nextInt(i);
                int j2 = rng.nextInt(i);
                ds[i] = ds[j1].add(ds[j2]);
            }
            else {
                int j1 = rng.nextInt(N) + N;
                int j2 = rng.nextInt(N) + N;
                ds[i] = ds[j1].multiply(ds[j2]);
            }
            try {
                check.update(ds[i].toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(4293496691L, check.getValue());
    }
}