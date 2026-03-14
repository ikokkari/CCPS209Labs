import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LemireFrequencySamplingTest {

    // --- buildSamplingTable explicit tests ---

    @Test public void testBuildFrequencyTableExplicit() {
        int[] a0 = {3, 4, 1, 0, 5, 2, 0, 2};
        int[][] e0 = { {3, 4, 1, 0, 5, 2, 0, 2}, {7, 1, 7, 2}, {8, 9}, {17} };
        int[][] r0 = LemireFrequencySampling.buildSamplingTable(a0);
        assertEquals(4, r0.length);
        assertTrue(Arrays.deepEquals(e0, r0));

        int[] a1 = {0, 7, 2, 1, 4, 0, 0, 0, 6, 2, 1, 0, 3, 3, 9, 1};
        int[][] e1 = { {0, 7, 2, 1, 4, 0, 0, 0, 6, 2, 1, 0, 3, 3, 9, 1},
                {7, 3, 4, 0, 8, 1, 6, 10}, {10, 4, 9, 16}, {14, 25}, {39} };
        int[][] r1 = LemireFrequencySampling.buildSamplingTable(a1);
        assertEquals(5, r1.length);
        assertTrue(Arrays.deepEquals(e1, r1));
    }

    @Test public void testBuildTableSizeTwo() {
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{3, 5});
        assertTrue(Arrays.deepEquals(new int[][]{{3, 5}, {8}}, t));
    }

    @Test public void testBuildTableSizeFour() {
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{1, 2, 3, 4});
        assertTrue(Arrays.deepEquals(new int[][]{{1, 2, 3, 4}, {3, 7}, {10}}, t));
    }

    @Test public void testBuildTableAllZeros() {
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{0, 0, 0, 0});
        assertTrue(Arrays.deepEquals(new int[][]{{0, 0, 0, 0}, {0, 0}, {0}}, t));
    }

    @Test public void testBuildTableSingleNonZero() {
        // Only position 2 has frequency; all sums propagate from there
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{0, 0, 5, 0});
        assertTrue(Arrays.deepEquals(new int[][]{{0, 0, 5, 0}, {0, 5}, {5}}, t));
    }

    @Test public void testBuildTableAllSame() {
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{3, 3, 3, 3});
        assertTrue(Arrays.deepEquals(new int[][]{{3, 3, 3, 3}, {6, 6}, {12}}, t));
    }

    @Test public void testBuildTableBottomRowIsTotal() {
        // Bottom row should always be a single element equal to total frequency sum
        int[] freq = {4, 0, 1, 5, 2, 2, 3, 4};
        int[][] t = LemireFrequencySampling.buildSamplingTable(freq);
        int total = 0;
        for (int f : freq) { total += f; }
        assertEquals(1, t[t.length - 1].length);
        assertEquals(total, t[t.length - 1][0]);
    }

    // --- updateSamplingTable explicit tests ---

    @Test public void testUpdateFrequencyTableExplicit() {
        int[][] freq = { {3, 4, 1, 0, 5, 2, 0, 2}, {7, 1, 7, 2}, {8, 9}, {17} };

        int[][] e0 = { {3, 4, 1, 0, 4, 2, 0, 2}, {7, 1, 6, 2}, {8, 8}, {16} };
        int r0 = LemireFrequencySampling.updateSamplingTable(freq, 9);
        assertEquals(4, r0);
        assertTrue(Arrays.deepEquals(e0, freq));

        int[][] e1 = { {3, 3, 1, 0, 4, 2, 0, 2}, {6, 1, 6, 2}, {7, 8}, {15} };
        int r1 = LemireFrequencySampling.updateSamplingTable(freq, 5);
        assertEquals(1, r1);
        assertTrue(Arrays.deepEquals(e1, freq));

        int[][] e2 = { {3, 3, 1, 0, 4, 2, 0, 1}, {6, 1, 6, 1}, {7, 7}, {14} };
        int r2 = LemireFrequencySampling.updateSamplingTable(freq, 13);
        assertEquals(7, r2);
        assertTrue(Arrays.deepEquals(e2, freq));

        int[][] e3 = { {2, 3, 1, 0, 4, 2, 0, 1}, {5, 1, 6, 1}, {6, 7}, {13} };
        int r3 = LemireFrequencySampling.updateSamplingTable(freq, 0);
        assertEquals(0, r3);
        assertTrue(Arrays.deepEquals(e3, freq));
    }

    @Test public void testUpdateSizeTwo() {
        // Size 2: r < left goes to position 0, r >= left goes to position 1
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{3, 5});
        // r=0,1,2 -> position 0; r=3..7 -> position 1
        assertEquals(0, LemireFrequencySampling.updateSamplingTable(
                deepCopy(t), 0));
        assertEquals(0, LemireFrequencySampling.updateSamplingTable(
                deepCopy(t), 2));
        assertEquals(1, LemireFrequencySampling.updateSamplingTable(
                deepCopy(t), 3));
        assertEquals(1, LemireFrequencySampling.updateSamplingTable(
                deepCopy(t), 7));
    }

    @Test public void testUpdateBoundaryValues() {
        // [1,2,3,4] cumulative: [1,3,6,10]
        // r=0 -> 0, r=1 -> 1, r=2 -> 1, r=3 -> 2, r=5 -> 2, r=6 -> 3, r=9 -> 3
        int[] freq = {1, 2, 3, 4};
        assertEquals(0, LemireFrequencySampling.updateSamplingTable(
                LemireFrequencySampling.buildSamplingTable(freq.clone()), 0));
        assertEquals(1, LemireFrequencySampling.updateSamplingTable(
                LemireFrequencySampling.buildSamplingTable(freq.clone()), 1));
        assertEquals(1, LemireFrequencySampling.updateSamplingTable(
                LemireFrequencySampling.buildSamplingTable(freq.clone()), 2));
        assertEquals(2, LemireFrequencySampling.updateSamplingTable(
                LemireFrequencySampling.buildSamplingTable(freq.clone()), 3));
        assertEquals(2, LemireFrequencySampling.updateSamplingTable(
                LemireFrequencySampling.buildSamplingTable(freq.clone()), 5));
        assertEquals(3, LemireFrequencySampling.updateSamplingTable(
                LemireFrequencySampling.buildSamplingTable(freq.clone()), 6));
        assertEquals(3, LemireFrequencySampling.updateSamplingTable(
                LemireFrequencySampling.buildSamplingTable(freq.clone()), 9));
    }

    @Test public void testUpdateSkipsZeroFrequencies() {
        // Only position 2 has frequency 5; all r values must land on position 2
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{0, 0, 5, 0});
        assertEquals(2, LemireFrequencySampling.updateSamplingTable(deepCopy(t), 0));
        assertEquals(2, LemireFrequencySampling.updateSamplingTable(deepCopy(t), 4));
    }

    @Test public void testUpdateDrainAllWithRZero() {
        // Repeatedly sampling r=0 should drain elements left to right
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{2, 1, 3, 2});
        int[] results = new int[8];
        for (int i = 0; i < 8; i++) {
            results[i] = LemireFrequencySampling.updateSamplingTable(t, 0);
        }
        // r=0 always picks leftmost available: 0,0,1,2,2,2,3,3
        assertArrayEquals(new int[]{0, 0, 1, 2, 2, 2, 3, 3}, results);
        // All frequencies should be zero
        for (int f : t[0]) { assertEquals(0, f); }
        assertEquals(0, t[t.length - 1][0]);
    }

    @Test public void testUpdateDrainAllWithRMax() {
        // Repeatedly sampling r=total-1 should drain right to left
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{1, 1, 1, 1});
        int[] results = new int[4];
        for (int i = 0; i < 4; i++) {
            results[i] = LemireFrequencySampling.updateSamplingTable(t, t[t.length - 1][0] - 1);
        }
        assertArrayEquals(new int[]{3, 2, 1, 0}, results);
        for (int f : t[0]) { assertEquals(0, f); }
    }

    @Test public void testUpdateMaintainsTableConsistency() {
        // After several updates, each row[i][j] should still equal row[i-1][2j] + row[i-1][2j+1]
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{3, 4, 1, 0, 5, 2, 0, 2});
        Random rng = new Random(42);
        for (int step = 0; step < 10; step++) {
            int total = t[t.length - 1][0];
            if (total == 0) break;
            LemireFrequencySampling.updateSamplingTable(t, rng.nextInt(total));
            // Verify structural invariant
            for (int i = 1; i < t.length; i++) {
                for (int j = 0; j < t[i].length; j++) {
                    assertEquals("Table inconsistency at row " + i + " col " + j,
                            t[i - 1][2 * j] + t[i - 1][2 * j + 1], t[i][j]);
                }
            }
        }
    }

    @Test public void testUpdateDecrementsByOne() {
        // Each update should decrease the total by exactly 1
        int[][] t = LemireFrequencySampling.buildSamplingTable(new int[]{4, 3, 2, 1});
        int total = t[t.length - 1][0]; // 10
        for (int i = 0; i < 10; i++) {
            assertEquals(total - i, t[t.length - 1][0]);
            LemireFrequencySampling.updateSamplingTable(t, 0);
        }
        assertEquals(0, t[t.length - 1][0]);
    }

    // Helper for deep copy
    private static int[][] deepCopy(int[][] table) {
        int[][] copy = new int[table.length][];
        for (int i = 0; i < table.length; i++) {
            copy[i] = table[i].clone();
        }
        return copy;
    }

    // --- CRC mass tests ---

    @Test public void massTestUpToFour() {
        massTest(4, 2804532126L);
    }

    @Test public void massTestUpToEight() {
        massTest(8, 1188956838L);
    }

    @Test public void massTestUpToSeventeen() {
        massTest(17, 2010571491L);
    }

    private void massTest(int levels, long expected) {
        Random rng = new Random(12345 + levels);
        CRC32 check = new CRC32();
        for(int curr = 1; curr <= levels; curr++) {
            int[] a = new int[1 << curr];
            for(int i = 0; i < 10 * levels; i++) {
                Arrays.fill(a, 0);
                for (int j = 0; j < a.length; j++) {
                    int p = rng.nextInt(a.length);
                    a[p] += 1 + rng.nextInt(10);
                }
                int sum = 0;
                for(int e: a) { sum += e; }
                int[][] table = LemireFrequencySampling.buildSamplingTable(a);
                int tlen = table.length - 1;
                assertEquals(tlen, curr);
                assertEquals(sum, table[tlen][0]);
                for(int j = 0; j < Math.min(a.length, 50); j++) {
                    int k = rng.nextInt(table[tlen][0]);
                    int result = LemireFrequencySampling.updateSamplingTable(table, k);
                    assertTrue(table[0][result] >= 0);
                    check.update(result);
                }
            }
        }
        assertEquals(expected, check.getValue());
    }
}