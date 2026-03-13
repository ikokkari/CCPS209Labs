import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PermutationsTestThree {

    // --- toFactoradic / fromFactoradic explicit tests ---

    @Test public void testFactoradicZero() {
        int[] coeff = new int[6];
        Permutations.toFactoradic(0, coeff);
        assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, coeff);
        assertEquals(0, Permutations.fromFactoradic(coeff));
    }

    @Test public void testFactoradicSmallValues() {
        int[] coeff = new int[6];

        // 1 = 1 * 1! => coeff[0]=1
        Permutations.toFactoradic(1, coeff);
        assertArrayEquals(new int[]{1, 0, 0, 0, 0, 0}, coeff);
        assertEquals(1, Permutations.fromFactoradic(coeff));

        // 2 = 1 * 2! => coeff[1]=1
        Arrays.fill(coeff, 0);
        Permutations.toFactoradic(2, coeff);
        assertArrayEquals(new int[]{0, 1, 0, 0, 0, 0}, coeff);
        assertEquals(2, Permutations.fromFactoradic(coeff));

        // 5 = 1*1! + 2*2! => coeff[0]=1, coeff[1]=2
        Arrays.fill(coeff, 0);
        Permutations.toFactoradic(5, coeff);
        assertArrayEquals(new int[]{1, 2, 0, 0, 0, 0}, coeff);
        assertEquals(5, Permutations.fromFactoradic(coeff));
    }

    @Test public void testFactoradicExactFactorials() {
        int[] coeff = new int[8];

        // 6 = 3! => coeff[2]=1
        Permutations.toFactoradic(6, coeff);
        assertArrayEquals(new int[]{0, 0, 1, 0, 0, 0, 0, 0}, coeff);

        // 24 = 4! => coeff[3]=1
        Arrays.fill(coeff, 0);
        Permutations.toFactoradic(24, coeff);
        assertArrayEquals(new int[]{0, 0, 0, 1, 0, 0, 0, 0}, coeff);

        // 120 = 5! => coeff[4]=1
        Arrays.fill(coeff, 0);
        Permutations.toFactoradic(120, coeff);
        assertArrayEquals(new int[]{0, 0, 0, 0, 1, 0, 0, 0}, coeff);

        // 720 = 6! => coeff[5]=1
        Arrays.fill(coeff, 0);
        Permutations.toFactoradic(720, coeff);
        assertArrayEquals(new int[]{0, 0, 0, 0, 0, 1, 0, 0}, coeff);
    }

    @Test public void testFactoradicFactorialMinusOne() {
        int[] coeff = new int[6];

        // n! - 1 has all max digits: coeff[k] = k+1 for k < n-1
        // 23 = 4!-1 => [1, 2, 3, 0, 0, 0]
        Permutations.toFactoradic(23, coeff);
        assertArrayEquals(new int[]{1, 2, 3, 0, 0, 0}, coeff);

        // 119 = 5!-1 => [1, 2, 3, 4, 0, 0]
        Arrays.fill(coeff, 0);
        Permutations.toFactoradic(119, coeff);
        assertArrayEquals(new int[]{1, 2, 3, 4, 0, 0}, coeff);

        // 719 = 6!-1 => [1, 2, 3, 4, 5, 0]
        Arrays.fill(coeff, 0);
        Permutations.toFactoradic(719, coeff);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 0}, coeff);
    }

    @Test public void testFactoradicSpecExample() {
        // 2168 = 1*2! + 1*3! + 3*6! = 2 + 6 + 2160 = 2168
        int[] coeff = new int[8];
        Permutations.toFactoradic(2168, coeff);
        assertArrayEquals(new int[]{0, 1, 1, 0, 0, 3, 0, 0}, coeff);
        assertEquals(2168, Permutations.fromFactoradic(coeff));
    }

    @Test public void testFactoradicRoundTrip() {
        // Various values round-trip correctly
        long[] vals = {0, 1, 2, 5, 6, 23, 24, 100, 119, 120, 719, 720, 2168, 1000000};
        for (long val : vals) {
            int[] coeff = new int[20];
            Permutations.toFactoradic(val, coeff);
            assertEquals("Round-trip failed for " + val, val, Permutations.fromFactoradic(coeff));
        }
    }

    // --- toLehmer / fromLehmer explicit tests ---

    @Test public void testLehmerIdentity() {
        // Identity permutation: zero inversions everywhere
        assertArrayEquals(new int[]{0, 0, 0}, Permutations.toLehmer(new int[]{0, 1, 2, 3}));
    }

    @Test public void testLehmerReverse() {
        // Reverse: maximum inversions. Position i has (n-1-i) inversions.
        assertArrayEquals(new int[]{3, 2, 1}, Permutations.toLehmer(new int[]{3, 2, 1, 0}));
        assertArrayEquals(new int[]{4, 3, 2, 1}, Permutations.toLehmer(new int[]{4, 3, 2, 1, 0}));
    }

    @Test public void testLehmerSpecExample() {
        // {5,6,2,3,1,0,4,7}: inversions are [5,5,2,2,1,0,0]
        assertArrayEquals(new int[]{5, 5, 2, 2, 1, 0, 0},
                Permutations.toLehmer(new int[]{5, 6, 2, 3, 1, 0, 4, 7}));
    }

    @Test public void testLehmerSmall() {
        // Size 2
        assertArrayEquals(new int[]{1}, Permutations.toLehmer(new int[]{1, 0}));
        assertArrayEquals(new int[]{0}, Permutations.toLehmer(new int[]{0, 1}));

        // Size 3
        assertArrayEquals(new int[]{2, 0}, Permutations.toLehmer(new int[]{2, 0, 1}));
        assertArrayEquals(new int[]{0, 1}, Permutations.toLehmer(new int[]{0, 2, 1}));
    }

    @Test public void testLehmerSingleSwap() {
        // Single adjacent swap at the front: [1,0,2,3] -> [1,0,0]
        assertArrayEquals(new int[]{1, 0, 0}, Permutations.toLehmer(new int[]{1, 0, 2, 3}));

        // Single swap at the back: [0,1,3,2] -> [0,0,1]
        assertArrayEquals(new int[]{0, 0, 1}, Permutations.toLehmer(new int[]{0, 1, 3, 2}));
    }

    @Test public void testLehmerFromLehmerRoundTrip() {
        int[][] perms = {
                {0}, {0, 1}, {1, 0}, {0, 1, 2, 3}, {3, 2, 1, 0},
                {2, 0, 1}, {0, 2, 1}, {1, 2, 0},
                {5, 6, 2, 3, 1, 0, 4, 7}, {2, 1, 4, 0, 5, 3}
        };
        for (int[] perm : perms) {
            int[] lehmer = Permutations.toLehmer(perm);
            int[] back = Permutations.fromLehmer(lehmer);
            assertArrayEquals("Lehmer round-trip failed for " + Arrays.toString(perm), perm, back);
        }
    }

    // --- toKey / fromKey explicit tests ---

    @Test public void testKeyIdentityIsZero() {
        // Identity permutation always maps to key 0
        for (int n = 1; n <= 5; n++) {
            int[] id = new int[n];
            for (int i = 0; i < n; i++) { id[i] = i; }
            assertEquals("Identity of size " + n + " should have key 0",
                    0, Permutations.toKey(id));
        }
    }

    @Test public void testKeyReverseIsMaxKey() {
        // Reverse permutation maps to key n!-1
        assertEquals(1, Permutations.toKey(new int[]{1, 0}));       // 2!-1
        assertEquals(5, Permutations.toKey(new int[]{2, 1, 0}));    // 3!-1
        assertEquals(23, Permutations.toKey(new int[]{3, 2, 1, 0})); // 4!-1
        assertEquals(119, Permutations.toKey(new int[]{4, 3, 2, 1, 0})); // 5!-1
    }

    @Test public void testFromKeyZeroIsIdentity() {
        for (int n = 1; n <= 5; n++) {
            int[] expected = new int[n];
            for (int i = 0; i < n; i++) { expected[i] = i; }
            assertArrayEquals(expected, Permutations.fromKey(0, n));
        }
    }

    @Test public void testFromKeyMaxIsReverse() {
        assertArrayEquals(new int[]{1, 0}, Permutations.fromKey(1, 2));
        assertArrayEquals(new int[]{2, 1, 0}, Permutations.fromKey(5, 3));
        assertArrayEquals(new int[]{3, 2, 1, 0}, Permutations.fromKey(23, 4));
        assertArrayEquals(new int[]{4, 3, 2, 1, 0}, Permutations.fromKey(119, 5));
    }

    @Test public void testKeyAllPermutationsN3() {
        // All 3! = 6 permutations in lexicographic order
        assertArrayEquals(new int[]{0, 1, 2}, Permutations.fromKey(0, 3));
        assertArrayEquals(new int[]{0, 2, 1}, Permutations.fromKey(1, 3));
        assertArrayEquals(new int[]{1, 0, 2}, Permutations.fromKey(2, 3));
        assertArrayEquals(new int[]{1, 2, 0}, Permutations.fromKey(3, 3));
        assertArrayEquals(new int[]{2, 0, 1}, Permutations.fromKey(4, 3));
        assertArrayEquals(new int[]{2, 1, 0}, Permutations.fromKey(5, 3));
    }

    @Test public void testKeySpecificValues() {
        // [1,0,2,3] is the 7th permutation of 4 (key=6)
        assertEquals(6, Permutations.toKey(new int[]{1, 0, 2, 3}));
        assertArrayEquals(new int[]{1, 0, 2, 3}, Permutations.fromKey(6, 4));

        // [0,1,3,2] has key 1 among 4-element permutations
        assertEquals(1, Permutations.toKey(new int[]{0, 1, 3, 2}));
        assertArrayEquals(new int[]{0, 1, 3, 2}, Permutations.fromKey(1, 4));

        // Larger example from spec verification
        assertEquals(29094, Permutations.toKey(new int[]{5, 6, 2, 3, 1, 0, 4, 7}));
        assertArrayEquals(new int[]{5, 6, 2, 3, 1, 0, 4, 7}, Permutations.fromKey(29094, 8));
    }

    @Test public void testKeyRoundTrip() {
        int[][] perms = {
                {0}, {1, 0}, {0, 1, 2, 3}, {3, 2, 1, 0},
                {2, 0, 1}, {5, 6, 2, 3, 1, 0, 4, 7}, {2, 1, 4, 0, 5, 3}
        };
        for (int[] perm : perms) {
            long key = Permutations.toKey(perm);
            int[] back = Permutations.fromKey(key, perm.length);
            assertArrayEquals("Key round-trip failed for " + Arrays.toString(perm), perm, back);
        }
    }

    @Test public void testKeySizeOne() {
        // Only one permutation of size 1: key 0
        assertEquals(0, Permutations.toKey(new int[]{0}));
        assertArrayEquals(new int[]{0}, Permutations.fromKey(0, 1));
    }

    // --- CRC mass tests ---

    @Test public void testFactoradicHundred() {
        testFactoradic(100, 6, 1614347970L);
    }

    @Test public void testFactoradicTenThousand() {
        testFactoradic(10_000, 11, 1386970017L);
    }

    @Test public void testFactoradicMillion() {
        testFactoradic(1_000_000, 16, 4173735546L);
    }

    private void testFactoradic(int n, int m, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        long curr = 1;
        int step = 2, goal = 10;
        int[] coeff = new int[m];
        for(int i = 0; i < n; i++) {
            Arrays.fill(coeff, 0);
            Permutations.toFactoradic(curr, coeff);
            try {
                check.update(Arrays.toString(coeff).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            long back = Permutations.fromFactoradic(coeff);
            assertEquals(curr, back);
            curr += 1 + rng.nextInt(step);
            if(i == goal) { goal = 2 * goal; step = 3 * step; }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testKeyHundred() {
        testKey(100);
    }

    @Test public void testKeyHundredThousand() {
        testKey(100_000);
    }

    private void testKey(long n) {
        Random rng = new Random(12345);
        int goal = 2, nn = 2;
        for(int i = 0; i < n; i++) {
            if(i == goal) { nn++; goal = 2 * goal; }
            int[] perm = new int[nn];
            for(int j = 0; j < nn; j++) { perm[j] = j; }
            for(int k = 0; k < nn; k++) {
                int s = rng.nextInt(nn - k) + k;
                int tmp = perm[k]; perm[k] = perm[s]; perm[s] = tmp;
            }
            long key = Permutations.toKey(perm);
            int[] back = Permutations.fromKey(key, nn);
            assertArrayEquals(perm, back);
        }
    }

    @Test public void testLehmerHundred() {
        testLehmer(100, 631991195L);
    }

    @Test public void testLehmerThousand() {
        testLehmer(1000, 334055023L);
    }

    @Test public void testLehmerTenThousand() {
        testLehmer(10_000, 4233414032L);
    }

    private void testLehmer(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int goal = 2, nn = 2;
        for(int i = 0; i < n; i++) {
            if(i == goal) { nn++; goal = 2 * goal; }
            int[] perm = new int[nn];
            for(int j = 0; j < nn; j++) { perm[j] = j; }
            for(int k = 0; k < nn; k++) {
                int s = rng.nextInt(nn - k) + k;
                int tmp = perm[k]; perm[k] = perm[s]; perm[s] = tmp;
            }
            int[] inv = Permutations.toLehmer(perm);
            try {
                check.update(Arrays.toString(inv).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            int[] back = Permutations.fromLehmer(inv);
            assertArrayEquals(perm, back);
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testKeyConversion() {
        long f = 1;
        for(int i = 2; i <= 9; i++) { f = f * i; }
        int[] perm, prev = null;
        for(long key = 0; key < f; key++) {
            perm = Permutations.fromKey(key, 9);
            // Each permutation must be lexicographically greater than the previous one.
            for(int p = 0; key > 0 && p <= 9; p++) {
                assertTrue(p < 9); // This loop must terminate before reaching p == n.
                if(perm[p] > prev[p]) { break; }
                assertEquals(perm[p], prev[p]);
            }
            prev = perm;
        }
    }
}