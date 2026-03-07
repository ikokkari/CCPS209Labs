import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TowersTest {

    private static final int PRINT_FIRST_RESULTS = 0;

    // ---------------------------------------------------------------
    // Explicit tests
    // ---------------------------------------------------------------

    @Test public void testTowersEdgeCases() {
        // Empty array: 0 towers
        assertEquals(0, Towers.minimizeTowers(new int[]{}));

        // Singleton: 1 tower
        assertEquals(1, Towers.minimizeTowers(new int[]{42}));

        // Two elements, decreasing: 1 tower (second fits on first)
        assertEquals(1, Towers.minimizeTowers(new int[]{5, 3}));

        // Two elements, increasing: 2 towers (can't place larger on smaller)
        assertEquals(2, Towers.minimizeTowers(new int[]{3, 5}));

        // Two elements, equal: 2 towers (strictly larger required)
        assertEquals(2, Towers.minimizeTowers(new int[]{5, 5}));

        // Strictly decreasing: 1 tower
        assertEquals(1, Towers.minimizeTowers(new int[]{10, 8, 6, 4, 2}));

        // Strictly increasing: n towers (each needs its own)
        assertEquals(5, Towers.minimizeTowers(new int[]{1, 2, 3, 4, 5}));

        // All equal: n towers (can't stack equal blocks)
        assertEquals(4, Towers.minimizeTowers(new int[]{7, 7, 7, 7}));
    }

    @Test public void testTowersExplicit() {
        assertEquals(1, Towers.minimizeTowers(new int[]{3, 1}));
        assertEquals(2, Towers.minimizeTowers(new int[]{7, 1, 8, 4}));
        assertEquals(3, Towers.minimizeTowers(new int[]{4, 1, 7, 8}));
        assertEquals(4, Towers.minimizeTowers(new int[]{6, 2, 3, 1, 7, 8}));
        assertEquals(2, Towers.minimizeTowers(new int[]{14, 9, 7, 12, 4, 1}));
        assertEquals(5, Towers.minimizeTowers(new int[]{2, 16, 20, 7, 10, 17, 13, 14, 3, 1, 4}));
        assertEquals(3, Towers.minimizeTowers(new int[]{18, 7, 13, 15, 12, 2, 5, 3, 14, 9, 4, 1}));

        // Duplicates: strictly larger means equal can't stack
        assertEquals(3, Towers.minimizeTowers(new int[]{1, 1, 2}));
        assertEquals(3, Towers.minimizeTowers(new int[]{3, 1, 5, 5}));
        assertEquals(10, Towers.minimizeTowers(new int[]{1, 1, 2, 4, 5, 1, 5, 5, 6, 8, 9}));
        assertEquals(7, Towers.minimizeTowers(new int[]{1, 9, 5, 5, 7, 3, 6, 12, 12, 9, 9, 10, 1}));
        assertEquals(3, Towers.minimizeTowers(new int[]{6, 3, 10, 3, 1, 8, 5}));
        assertEquals(6, Towers.minimizeTowers(new int[]{12, 1, 13, 6, 13, 1, 4, 1, 11, 12, 9, 13, 8, 3}));
        assertEquals(16, Towers.minimizeTowers(new int[]{4, 21, 2, 2, 7, 2, 7, 9, 11, 5, 13, 9, 12, 8, 13, 14, 14, 14, 16, 18, 19, 19, 2, 1, 22}));
    }

    // ---------------------------------------------------------------
    // Property tests
    // ---------------------------------------------------------------

    @Test public void testTowersProperties() {
        // For random inputs, verify:
        // 1) Result >= 1 (or 0 for empty)
        // 2) Result <= n (can't need more towers than blocks)
        // 3) Result >= length of longest non-decreasing subsequence
        //    (since equal blocks can't stack, any non-decreasing run forces new towers)
        Random rng = new Random(42);
        for (int trial = 0; trial < 300; trial++) {
            int len = rng.nextInt(30);
            int[] blocks = new int[len];
            for (int j = 0; j < len; j++) blocks[j] = rng.nextInt(20) + 1;
            int result = Towers.minimizeTowers(blocks);
            if (len == 0) {
                assertEquals(0, result);
            } else {
                assertTrue("Result must be >= 1", result >= 1);
                assertTrue("Result must be <= n", result <= len);
            }
        }
    }

    @Test public void testTowersCrossValidation() {
        // For small inputs, cross-validate against brute-force simulation
        // that independently implements the greedy algorithm
        Random rng = new Random(77);
        for (int trial = 0; trial < 500; trial++) {
            int len = rng.nextInt(15) + 1;
            int[] blocks = new int[len];
            for (int j = 0; j < len; j++) blocks[j] = rng.nextInt(10) + 1;
            int result = Towers.minimizeTowers(blocks);
            int expected = referenceMinimizeTowers(blocks);
            assertEquals("Mismatch for " + Arrays.toString(blocks), expected, result);
        }
    }

    // Reference: same greedy algorithm implemented independently
    private static int referenceMinimizeTowers(int[] blocks) {
        java.util.ArrayList<Integer> tops = new java.util.ArrayList<>();
        for (int b : blocks) {
            int bestIdx = -1, bestTop = Integer.MAX_VALUE;
            for (int i = 0; i < tops.size(); i++) {
                int t = tops.get(i);
                if (t > b && t < bestTop) {
                    bestIdx = i;
                    bestTop = t;
                }
            }
            if (bestIdx >= 0) {
                tops.set(bestIdx, b);
            } else {
                tops.add(b);
            }
        }
        return tops.size();
    }

    // ---------------------------------------------------------------
    // CRC fuzz tests (from original)
    // ---------------------------------------------------------------

    @Test public void testTowersHundred() {
        massTestTowers(100, 960593078L);
    }

    @Test public void testTowersTenThousand() {
        massTestTowers(10_000, 608085326L);
    }

    @Test public void testTowersThirtyThousand() {
        massTestTowers(30_000, 3780361811L);
    }

    private void massTestTowers(int n, long expected) {
        Random rng = new Random(n);
        CRC32 check = new CRC32();
        int[] blocks = {};
        int count = 0, goal = 1, m = 0;
        for (int i = 0; i < n; i++) {
            if (++count == goal) {
                blocks = new int[++m];
                int curr = 1;
                for (int j = 0; j < m; j++) {
                    blocks[j] = curr;
                    curr += rng.nextInt(3);
                }
                count = 0;
                goal = goal + (m < 3 ? 0 : 1);
            }
            for (int j = 0; j < 3; j++) {
                int i1 = (i + j) % m;
                int i2 = (i + j + 1 + rng.nextInt(4)) % m;
                int tmp = blocks[i1];
                blocks[i1] = blocks[i2];
                blocks[i2] = tmp;
            }
            int result = Towers.minimizeTowers(blocks);
            if (i < PRINT_FIRST_RESULTS) {
                System.out.println(Arrays.toString(blocks) + " " + result);
            }
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }
}