import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J10Test {

    private static void fill(boolean[] v, Random rng) {
        for (int i = 0; i < v.length; i++) {
            v[i] = rng.nextBoolean();
        }
        v[rng.nextInt(v.length)] = true;
    }

    private static boolean[] conv(String bits) {
        int n = bits.length();
        boolean[] result = new boolean[n];
        for (int i = 0; i < n; i++) {
            result[i] = bits.charAt(i) == '1';
        }
        return result;
    }

    // Reference implementation of the four counts
    private static int[] computeCounts(boolean[] v1, boolean[] v2) {
        int n00 = 0, n01 = 0, n10 = 0, n11 = 0;
        for (int i = 0; i < v1.length; i++) {
            if (!v1[i] && !v2[i]) n00++;
            else if (!v1[i] && v2[i]) n01++;
            else if (v1[i] && !v2[i]) n10++;
            else n11++;
        }
        return new int[]{n00, n01, n10, n11};
    }

    // Reference implementations for cross-validation
    private static Fraction refJaccard(boolean[] v1, boolean[] v2) {
        int[] c = computeCounts(v1, v2);
        int num = c[1] + c[2], den = c[3] + c[2] + c[1];
        return num > 0 ? new Fraction(num, den) : new Fraction(0);
    }

    private static Fraction refMatching(boolean[] v1, boolean[] v2) {
        int[] c = computeCounts(v1, v2);
        return new Fraction(c[1] + c[2], v1.length);
    }

    private static Fraction refDice(boolean[] v1, boolean[] v2) {
        int[] c = computeCounts(v1, v2);
        int num = c[1] + c[2], den = 2 * c[3] + c[2] + c[1];
        return num > 0 ? new Fraction(num, den) : new Fraction(0);
    }

    private static Fraction refRogersTanimono(boolean[] v1, boolean[] v2) {
        int[] c = computeCounts(v1, v2);
        int num = 2 * (c[1] + c[2]), den = c[3] + 2 * (c[2] + c[1]) + c[0];
        return num > 0 ? new Fraction(num, den) : new Fraction(0);
    }

    private static Fraction refRussellRao(boolean[] v1, boolean[] v2) {
        int[] c = computeCounts(v1, v2);
        return new Fraction(c[1] + c[2] + c[0], v1.length);
    }

    private static Fraction refSokalSneath(boolean[] v1, boolean[] v2) {
        int[] c = computeCounts(v1, v2);
        int num = 2 * (c[1] + c[2]), den = c[3] + 2 * (c[2] + c[1]);
        return num > 0 ? new Fraction(num, den) : new Fraction(0);
    }

    // ---------------------------------------------------------------
    // Explicit tests (from original, kept intact)
    // ---------------------------------------------------------------

    @Test public void testDissimilarityExplicit() {
        boolean[] f1 = conv("10101"), s1 = conv("10101");
        assertEquals("0", P2J10.matchingDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.jaccardDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.diceDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.rogersTanimonoDissimilarity(f1, s1).toString());
        assertEquals("2/5", P2J10.russellRaoDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.sokalSneathDissimilarity(f1, s1).toString());

        boolean[] f2 = conv("01111"), s2 = conv("00100");
        assertEquals("3/5", P2J10.matchingDissimilarity(f2, s2).toString());
        assertEquals("3/4", P2J10.jaccardDissimilarity(f2, s2).toString());
        assertEquals("3/5", P2J10.diceDissimilarity(f2, s2).toString());
        assertEquals("3/4", P2J10.rogersTanimonoDissimilarity(f2, s2).toString());
        assertEquals("4/5", P2J10.russellRaoDissimilarity(f2, s2).toString());
        assertEquals("6/7", P2J10.sokalSneathDissimilarity(f2, s2).toString());

        boolean[] f3 = conv("01110"), s3 = conv("11100");
        assertEquals("2/5", P2J10.matchingDissimilarity(f3, s3).toString());
        assertEquals("1/2", P2J10.jaccardDissimilarity(f3, s3).toString());
        assertEquals("1/3", P2J10.diceDissimilarity(f3, s3).toString());
        assertEquals("4/7", P2J10.rogersTanimonoDissimilarity(f3, s3).toString());
        assertEquals("3/5", P2J10.russellRaoDissimilarity(f3, s3).toString());
        assertEquals("2/3", P2J10.sokalSneathDissimilarity(f3, s3).toString());

        boolean[] f4 = conv("10011"), s4 = conv("11100");
        assertEquals("4/5", P2J10.matchingDissimilarity(f4, s4).toString());
        assertEquals("4/5", P2J10.jaccardDissimilarity(f4, s4).toString());
        assertEquals("2/3", P2J10.diceDissimilarity(f4, s4).toString());
        assertEquals("8/9", P2J10.rogersTanimonoDissimilarity(f4, s4).toString());
        assertEquals("4/5", P2J10.russellRaoDissimilarity(f4, s4).toString());
        assertEquals("8/9", P2J10.sokalSneathDissimilarity(f4, s4).toString());

        // Completely disjoint: n11=0
        boolean[] f7 = conv("01101"), s7 = conv("10010");
        assertEquals("1", P2J10.matchingDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.jaccardDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.diceDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.rogersTanimonoDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.russellRaoDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.sokalSneathDissimilarity(f7, s7).toString());

        // All true in both: n01=n10=0, n00=0, n11=n
        boolean[] ft = conv("11111"), st = conv("11111");
        assertEquals("0", P2J10.matchingDissimilarity(ft, st).toString());
        assertEquals("0", P2J10.jaccardDissimilarity(ft, st).toString());
        assertEquals("0", P2J10.diceDissimilarity(ft, st).toString());
        assertEquals("0", P2J10.rogersTanimonoDissimilarity(ft, st).toString());
        assertEquals("0", P2J10.russellRaoDissimilarity(ft, st).toString());
        assertEquals("0", P2J10.sokalSneathDissimilarity(ft, st).toString());
    }

    // ---------------------------------------------------------------
    // Symmetry: d(v1,v2) == d(v2,v1) for all metrics
    // ---------------------------------------------------------------

    @Test public void testSymmetry() {
        Random rng = new Random(42);
        for (int trial = 0; trial < 200; trial++) {
            int n = rng.nextInt(20) + 2;
            boolean[] v1 = new boolean[n], v2 = new boolean[n];
            fill(v1, rng);
            fill(v2, rng);
            assertEquals("Jaccard symmetry",
                    P2J10.jaccardDissimilarity(v1, v2).toString(),
                    P2J10.jaccardDissimilarity(v2, v1).toString());
            assertEquals("Matching symmetry",
                    P2J10.matchingDissimilarity(v1, v2).toString(),
                    P2J10.matchingDissimilarity(v2, v1).toString());
            assertEquals("Dice symmetry",
                    P2J10.diceDissimilarity(v1, v2).toString(),
                    P2J10.diceDissimilarity(v2, v1).toString());
            assertEquals("RT symmetry",
                    P2J10.rogersTanimonoDissimilarity(v1, v2).toString(),
                    P2J10.rogersTanimonoDissimilarity(v2, v1).toString());
            assertEquals("RR symmetry",
                    P2J10.russellRaoDissimilarity(v1, v2).toString(),
                    P2J10.russellRaoDissimilarity(v2, v1).toString());
            assertEquals("SS symmetry",
                    P2J10.sokalSneathDissimilarity(v1, v2).toString(),
                    P2J10.sokalSneathDissimilarity(v2, v1).toString());
        }
    }

    // ---------------------------------------------------------------
    // Cross-validate against reference implementations
    // ---------------------------------------------------------------

    @Test public void testCrossValidation() {
        Random rng = new Random(99999);
        for (int trial = 0; trial < 300; trial++) {
            int n = rng.nextInt(30) + 2;
            boolean[] v1 = new boolean[n], v2 = new boolean[n];
            fill(v1, rng);
            fill(v2, rng);
            assertEquals("Jaccard mismatch",
                    refJaccard(v1, v2).toString(),
                    P2J10.jaccardDissimilarity(v1, v2).toString());
            assertEquals("Matching mismatch",
                    refMatching(v1, v2).toString(),
                    P2J10.matchingDissimilarity(v1, v2).toString());
            assertEquals("Dice mismatch",
                    refDice(v1, v2).toString(),
                    P2J10.diceDissimilarity(v1, v2).toString());
            assertEquals("RT mismatch",
                    refRogersTanimono(v1, v2).toString(),
                    P2J10.rogersTanimonoDissimilarity(v1, v2).toString());
            assertEquals("RR mismatch",
                    refRussellRao(v1, v2).toString(),
                    P2J10.russellRaoDissimilarity(v1, v2).toString());
            assertEquals("SS mismatch",
                    refSokalSneath(v1, v2).toString(),
                    P2J10.sokalSneathDissimilarity(v1, v2).toString());
        }
    }

    // ---------------------------------------------------------------
    // Range check: all results in [0, 1]
    // ---------------------------------------------------------------

    @Test public void testRange() {
        Fraction ZERO = new Fraction(0);
        Fraction ONE = new Fraction(1);
        Random rng = new Random(54321);
        for (int trial = 0; trial < 200; trial++) {
            int n = rng.nextInt(20) + 2;
            boolean[] v1 = new boolean[n], v2 = new boolean[n];
            fill(v1, rng);
            fill(v2, rng);
            Fraction[] results = {
                    P2J10.jaccardDissimilarity(v1, v2),
                    P2J10.matchingDissimilarity(v1, v2),
                    P2J10.diceDissimilarity(v1, v2),
                    P2J10.rogersTanimonoDissimilarity(v1, v2),
                    P2J10.russellRaoDissimilarity(v1, v2),
                    P2J10.sokalSneathDissimilarity(v1, v2)
            };
            String[] names = {"Jaccard", "Matching", "Dice", "RT", "RR", "SS"};
            for (int i = 0; i < results.length; i++) {
                assertTrue(names[i] + " < 0", results[i].compareTo(ZERO) >= 0);
                assertTrue(names[i] + " > 1", results[i].compareTo(ONE) <= 0);
            }
        }
    }

    // ---------------------------------------------------------------
    // CRC fuzz tests (from original)
    // ---------------------------------------------------------------

    private static final int N = 1000;

    @Test public void testJaccard() { testDissimilarityMass(619021331L, 0); }
    @Test public void testMatching() { testDissimilarityMass(2582992579L, 1); }
    @Test public void testDice() { testDissimilarityMass(864445653L, 2); }
    @Test public void testRogersTanimono() { testDissimilarityMass(2631246168L, 3); }
    @Test public void testRussellRao() { testDissimilarityMass(3219060315L, 4); }
    @Test public void testSokalSneath() { testDissimilarityMass(737788739L, 5); }

    private void testDissimilarityMass(long expected, int mode) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for (int i = 0; i < P2J10Test.N; i++) {
            int nn = 5 + i / 20;
            boolean[] v1 = new boolean[nn], v2 = new boolean[nn];
            fill(v1, rng);
            fill(v2, rng);
            Fraction result;
            if (mode == 0) result = P2J10.jaccardDissimilarity(v1, v2);
            else if (mode == 1) result = P2J10.matchingDissimilarity(v1, v2);
            else if (mode == 2) result = P2J10.diceDissimilarity(v1, v2);
            else if (mode == 3) result = P2J10.rogersTanimonoDissimilarity(v1, v2);
            else if (mode == 4) result = P2J10.russellRaoDissimilarity(v1, v2);
            else result = P2J10.sokalSneathDissimilarity(v1, v2);
            check.update(result.getNum().intValue());
            check.update(result.getDen().intValue());
        }
        assertEquals(expected, check.getValue());
    }
}