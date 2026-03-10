import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class DistanceTestThree {

    private static final int SEED = 12345;
    private static final int PREC = 30;
    private static final Distance ZERO = new Distance(0, 1);

    // --- equals tests ---

    @Test public void testEqualsBasic() {
        Distance d1 = new Distance(3, 5);
        Distance d2 = new Distance(3, 5);
        Distance d3 = new Distance(4, 5);
        assertEquals(d1, d2);
        assertNotEquals(d1, d3);
        // Reflexive.
        assertEquals(d1, d1);
    }

    @Test public void testEqualsWithSquareExtraction() {
        // Distance(1, 20) and Distance(2, 5) should be equal: both are 2Sqrt[5].
        Distance d1 = new Distance(1, 20);
        Distance d2 = new Distance(2, 5);
        assertEquals(d1, d2);
    }

    @Test public void testEqualsZero() {
        Distance z1 = new Distance(0, 1);
        Distance z2 = new Distance(0, 42);
        assertEquals(z1, z2);
    }

    @Test public void testEqualsWithNonDistance() {
        Distance d = new Distance(3, 5);
        assertNotEquals("hello", d);
        assertNotEquals(d, null);
        assertNotEquals(d, Integer.valueOf(3));
    }

    // --- hashCode tests ---

    @Test public void testHashCodeConsistency() {
        Distance d = new Distance(3, 5);
        assertEquals(d.hashCode(), d.hashCode());
    }

    @Test public void testHashCodeEqualObjects() {
        Distance d1 = new Distance(3, 5);
        Distance d2 = new Distance(3, 5);
        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test public void testHashCodeWithSquareExtraction() {
        Distance d1 = new Distance(1, 20);
        Distance d2 = new Distance(2, 5);
        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    // --- approximate tests ---

    @Test public void testApproximateSimple() {
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        // Distance(3, 1) = 3, should approximate to 3.
        Distance d1 = new Distance(3, 1);
        BigDecimal a1 = d1.approximate(mc);
        assertEquals(0, a1.compareTo(new BigDecimal("3")));

        // Distance(0, 1) = 0.
        BigDecimal a0 = ZERO.approximate(mc);
        assertEquals(0, a0.compareTo(BigDecimal.ZERO));

        // Distance(1, 4) = 2 (after square extraction).
        Distance d2 = new Distance(1, 4);
        BigDecimal a2 = d2.approximate(mc);
        assertEquals(0, a2.compareTo(new BigDecimal("2")));
    }

    @Test public void testApproximateMassTest() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        MathContext mc2 = new MathContext(PREC + 2, RoundingMode.HALF_UP);
        BigDecimal epsilon = new BigDecimal(1).scaleByPowerOfTen(PREC);
        for(int i = 1; i < 100; i++) {
            Distance d = ZERO;
            BigDecimal prev = new BigDecimal(0, mc2);
            for(int j = 0; j * j < i; j++) {
                int whole = rng.nextInt(i + 3) + 1;
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 2;
                Distance dd = new Distance(whole, base);
                d = d.add(dd);

                BigDecimal curr = d.approximate(mc2);
                try {
                    check.update(curr.toString().getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) {}
                BigDecimal diff = curr.subtract(prev);
                assertTrue(diff.abs().compareTo(epsilon) < 0);
                prev = curr;
            }
        }
        assertEquals(4064272570L, check.getValue());
    }

    // --- compareTo tests ---

    @Test public void testCompareToReflexive() {
        Distance d = new Distance(java.util.Map.of(3, 2, 7, -1, 11, 5));
        assertEquals(0, d.compareTo(d));
    }

    @Test public void testCompareToEqualDistances() {
        Distance d1 = new Distance(3, 5);
        Distance d2 = new Distance(3, 5);
        assertEquals(0, d1.compareTo(d2));
    }

    @Test public void testCompareToSimple() {
        Distance d1 = new Distance(3, 1); // 3
        Distance d2 = new Distance(1, 2); // Sqrt[2] ≈ 1.414
        assertTrue(d1.compareTo(d2) > 0);
        assertTrue(d2.compareTo(d1) < 0);
    }

    @Test public void testCompareToZero() {
        Distance pos = new Distance(1, 2);
        Distance neg = new Distance(-1, 2);
        assertTrue(pos.compareTo(ZERO) > 0);
        assertTrue(ZERO.compareTo(pos) < 0);
        assertTrue(neg.compareTo(ZERO) < 0);
        assertTrue(ZERO.compareTo(neg) > 0);
        assertEquals(0, ZERO.compareTo(ZERO));
    }

    @Test public void testCompareToCloseSums() {
        // From a Hacker News comment — these sums agree up to ~20 decimal places.
        int[][][] testCases = {
                {{1000000, 1}, {1000018, 1}, {1000036, 1}, {1000059, 1}, {1000083, 1}},
                {{1000003, 1}, {1000011, 1}, {1000048, 1}, {1000050, 1}, {1000084, 1}},
                {{1000000, 1}, {1000018, 1}, {1000036, 1}, {1000059, 1}, {1000081, 1}},
                {{1000003, 1}, {1000011, 1}, {1000046, 1}, {1000050, 1}, {1000084, 1}},
                {{1000000, 1}, {1000018, 1}, {1000036, 1}, {1000060, 1}, {1000083, 1}},
                {{1000004, 1}, {1000011, 1}, {1000048, 1}, {1000052, 1}, {1000084, 1}},
        };

        ArrayList<Distance> distances = new ArrayList<>();
        for(int[][] testCase : testCases) {
            TreeMap<Integer, Integer> coeff = new TreeMap<>();
            for(int[] co : testCase) {
                coeff.put(co[0], co[1]);
            }
            distances.add(new Distance(coeff));
        }

        assertTrue(distances.get(0).compareTo(distances.get(1)) > 0);
        assertTrue(distances.get(1).compareTo(distances.get(2)) > 0);
        assertTrue(distances.get(2).compareTo(distances.get(3)) > 0);
        // Antisymmetry.
        assertTrue(distances.get(1).compareTo(distances.get(0)) < 0);
        assertTrue(distances.get(2).compareTo(distances.get(1)) < 0);
        assertTrue(distances.get(3).compareTo(distances.get(2)) < 0);
        // Larger variants.
        assertTrue(distances.get(4).compareTo(distances.get(0)) > 0);
        assertTrue(distances.get(5).compareTo(distances.get(1)) > 0);
        assertTrue(distances.get(5).compareTo(distances.get(4)) > 0);
    }

    @Test public void testCompareToMassTest() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int N = 40;
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
                ds[i] = ds[j1].add(ds[j2]);
            }
        }
        for(int i = 0; i < 3 * N; i++) {
            for(int j = i + 1; j < 3 * N; j++) {
                int comp = ds[i].compareTo(ds[j]);
                comp = Integer.compare(comp, 0);
                check.update(comp);
            }
        }
        assertEquals(1484089080L, check.getValue());
    }

    // --- Collection lockstep test ---

    @Test public void massTestCollections() {
        int N = 1000;
        Random rng = new Random(SEED);
        HashSet<Distance> hs = new HashSet<>();
        TreeSet<Distance> ts = new TreeSet<>();

        for(int i = 0; i < N; i++) {
            Distance d = new Distance(0, 1);
            for(int k = 0; k <= i % 20; k++) {
                int whole = rng.nextInt(i + 3);
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 1;
                d = d.add(new Distance(whole, base));
            }
            assertEquals(hs.contains(d), ts.contains(d));
            hs.add(d);
            ts.add(d);
            assertFalse(hs.size() < ts.size());
            assertFalse(hs.size() > ts.size());
        }
        for(Distance d : hs) {
            assertTrue(ts.contains(d));
        }
        for(Distance d : ts) {
            assertTrue(hs.contains(d));
        }
    }
}