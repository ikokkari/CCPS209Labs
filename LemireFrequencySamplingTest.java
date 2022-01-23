import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LemireFrequencySamplingTest {

    @Test public void testBuildFrequencyTableExplicit() {
        // Original frequencies, these add up to 17.
        int[] a0 = {3, 4, 1, 0, 5, 2, 0, 2};
        // Expected frequency table.
        int[][] e0 = { {3, 4, 1, 0, 5, 2, 0, 2}, {7, 1, 7, 2}, {8, 9}, {17} };
        // Actual frequency table returned by student method.
        int[][] r0 = LemireFrequencySampling.buildSamplingTable(a0);
        // The rows of the result must be as expected.
        assertEquals(4,r0.length);
        assertTrue(Arrays.deepEquals(e0, r0));

        // Original frequencies, these add up to 39.
        int[] a1 = {0, 7, 2, 1, 4, 0, 0, 0, 6, 2, 1, 0, 3, 3, 9, 1};
        // Expected frequency table.
        int[][] e1 = { {0, 7, 2, 1, 4, 0, 0, 0, 6, 2, 1, 0, 3, 3, 9, 1},
                {7, 3, 4, 0, 8, 1, 6, 10}, {10, 4, 9, 16}, {14, 25}, {39} };
        // Actual frequency table returned by student method.
        int[][] r1 = LemireFrequencySampling.buildSamplingTable(a1);
        // The rows of the result must be as expected.
        assertEquals(5, r1.length);
        assertTrue(Arrays.deepEquals(e1, r1));
    }

    @Test public void testUpdateFrequencyTableExplicit() {
        // Original frequency table.
        int[][] freq = { {3, 4, 1, 0, 5, 2, 0, 2}, {7, 1, 7, 2}, {8, 9}, {17} };

        // Expected frequency table after deleting 9.
        int[][] e0 = { {3, 4, 1, 0, 4, 2, 0, 2}, {7, 1, 6, 2}, {8, 8}, {16} };
        // Actual frequency table after that deletion.
        int r0 = LemireFrequencySampling.updateSamplingTable(freq, 9);
        // 9 falls into position 4.
        assertEquals(4, r0);
        assertTrue(Arrays.deepEquals(e0, freq));

        // Expected frequency table after deleting 5.
        int[][] e1 = { {3, 3, 1, 0, 4, 2, 0, 2}, {6, 1, 6, 2}, {7, 8}, {15} };
        // Actual frequency table after that deletion.
        int r1 = LemireFrequencySampling.updateSamplingTable(freq, 5);
        // 5 falls into position 1.
        assertEquals(1, r1);
        assertTrue(Arrays.deepEquals(e1, freq));

        // Expected frequency table after deleting 13.
        int[][] e2 = { {3, 3, 1, 0, 4, 2, 0, 1}, {6, 1, 6, 1}, {7, 7}, {14} };
        // Actual frequency table after that deletion.
        int r2 = LemireFrequencySampling.updateSamplingTable(freq, 13);
        // 13 falls into position 7.
        assertEquals(7, r2);
        assertTrue(Arrays.deepEquals(e2, freq));

        // Expected frequency table after deleting 0.
        int[][] e3 = { {2, 3, 1, 0, 4, 2, 0, 1}, {5, 1, 6, 1}, {6, 7}, {13} };
        // Actual frequency table after that deletion.
        int r3 = LemireFrequencySampling.updateSamplingTable(freq, 0);
        // 0 falls into position 0.
        assertEquals(0, r3);
        assertTrue(Arrays.deepEquals(e3, freq));
    }

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