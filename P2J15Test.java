import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class P2J15Test {

    // ---------------------------------------------------------------
    // findClosestElements tests
    // ---------------------------------------------------------------

    @Test public void testFindClosestElementsExplicit() {
        // Original tests
        assertArrayEquals(new int[]{1, 2, 3, 4}, P2J15.findClosestElements(new int[]{1, 2, 3, 4, 5}, 3, 4));
        assertArrayEquals(new int[]{8, 10, 11, 13}, P2J15.findClosestElements(new int[]{1, 2, 8, 10, 11, 13, 22}, 8, 4));
        assertArrayEquals(new int[]{3, 4, 10}, P2J15.findClosestElements(new int[]{3, 4, 10, 20, 30, 40}, 10, 3));
        assertArrayEquals(new int[]{1, 10}, P2J15.findClosestElements(new int[]{1, 10, 19}, 10, 2));
        assertArrayEquals(new int[]{11, 22, 33}, P2J15.findClosestElements(new int[]{4, 5, 10, 11, 22, 33}, 33, 3));
        assertArrayEquals(new int[]{6}, P2J15.findClosestElements(new int[]{6, 7, 10, 12, 20, 28}, 6, 1));
        assertArrayEquals(new int[]{10, 12, 18, 25, 26, 32}, P2J15.findClosestElements(new int[]{5, 10, 12, 18, 25, 26, 32}, 25, 6));

        // k = entire array
        assertArrayEquals(new int[]{1, 2, 3}, P2J15.findClosestElements(new int[]{1, 2, 3}, 2, 3));

        // x at start of array
        assertArrayEquals(new int[]{1, 2, 3}, P2J15.findClosestElements(new int[]{1, 2, 3, 10, 20}, 1, 3));

        // x at end of array
        assertArrayEquals(new int[]{10, 20}, P2J15.findClosestElements(new int[]{1, 2, 3, 10, 20}, 20, 2));

        // Singleton array
        assertArrayEquals(new int[]{42}, P2J15.findClosestElements(new int[]{42}, 42, 1));

        // Tiebreak: equidistant elements, prefer smaller
        // {0, 5, 10}: x=5, k=2. Dist to 0 is 5, dist to 10 is 5. Prefer 0 (smaller).
        assertArrayEquals(new int[]{0, 5}, P2J15.findClosestElements(new int[]{0, 5, 10}, 5, 2));

        // Spec example
        assertArrayEquals(new int[]{8, 10, 11, 13}, P2J15.findClosestElements(new int[]{1, 2, 8, 10, 11, 13, 22}, 10, 4));
    }

    @Test public void testFindClosestElementsProperties() {
        // For random inputs, verify:
        // 1) Result has exactly k elements
        // 2) Result is sorted
        // 3) All elements are from the original array
        // 4) Result elements are contiguous in the original (closest k must form a window)
        Random rng = new Random(42);
        for (int trial = 0; trial < 300; trial++) {
            int m = rng.nextInt(15) + 2;
            int[] a = new int[m];
            a[0] = rng.nextInt(10);
            for (int j = 1; j < m; j++) a[j] = a[j - 1] + 1 + rng.nextInt(5);
            int idx = rng.nextInt(m);
            int x = a[idx];
            int k = 1 + rng.nextInt(m);
            int[] result = P2J15.findClosestElements(a, x, k);
            assertEquals("Wrong length", k, result.length);
            for (int j = 1; j < k; j++) {
                assertTrue("Not sorted", result[j] > result[j - 1]);
            }
            // Must be a contiguous subarray of a
            int start = Arrays.binarySearch(a, result[0]);
            assertTrue("First element not in array", start >= 0);
            for (int j = 0; j < k; j++) {
                assertEquals("Not contiguous at j=" + j, a[start + j], result[j]);
            }
        }
    }

    @Test public void testFindClosestElementsCrossValidation() {
        // Cross-validate against the LeetCode reference implementation
        Random rng = new Random(77);
        for (int trial = 0; trial < 500; trial++) {
            int m = rng.nextInt(20) + 2;
            int[] a = new int[m];
            a[0] = rng.nextInt(10);
            for (int j = 1; j < m; j++) a[j] = a[j - 1] + 1 + rng.nextInt(5);
            int idx = rng.nextInt(m);
            int x = a[idx];
            int k = 1 + rng.nextInt(m);
            int[] expected = P2J15.findClosestElementsLeet(a, x, k);
            int[] actual = P2J15.findClosestElements(a, x, k);
            assertArrayEquals("Mismatch for a=" + Arrays.toString(a) + " x=" + x + " k=" + k,
                    expected, actual);
        }
    }

    @Test public void testFindClosestElementsHundred() {
        testFindClosestElements(100, 388989778L);
    }

    @Test public void testFindClosestElementsTenThousand() {
        testFindClosestElements(10000, 944557431L);
    }

    @Test public void testFindClosestElementsHundredThousand() {
        testFindClosestElements(100000, 1956098612L);
    }

    private void testFindClosestElements(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345 + n);
        int count = 0, goal = 3, m = 7;
        for (int i = 0; i < n; i++) {
            int[] a = new int[m];
            a[0] = rng.nextInt(m);
            for (int j = 1; j < m; j++) {
                a[j] = a[j - 1] + 1 + rng.nextInt(m);
            }
            int idx = rng.nextInt(m);
            int x = a[idx];
            int k = 1 + rng.nextInt(m - 1);
            int[] result = P2J15.findClosestElements(a, x, k);
            check.update(result.length);
            for (int j = 0; j < result.length; j++) {
                check.update(result[j]);
            }
            if (++count == goal) {
                count = 0; goal += 2; m++;
            }
        }
        assertEquals(expected, check.getValue());
    }

    // ---------------------------------------------------------------
    // countSubarraysWithSum tests
    // ---------------------------------------------------------------

    @Test public void testCountSubarraysWithSumExplicit() {
        // Empty and singleton
        assertEquals(0, P2J15.countSubarraysWithSum(new int[]{}, 42));
        assertEquals(1, P2J15.countSubarraysWithSum(new int[]{42}, 42));
        assertEquals(0, P2J15.countSubarraysWithSum(new int[]{42}, 99));

        // Original tests
        assertEquals(2, P2J15.countSubarraysWithSum(new int[]{2, 2, 2}, 4));
        assertEquals(0, P2J15.countSubarraysWithSum(new int[]{2, 2, 2}, 5));
        assertEquals(1, P2J15.countSubarraysWithSum(new int[]{2, 2, 2}, 6));

        // Spec example
        assertEquals(3, P2J15.countSubarraysWithSum(new int[]{6, 3, 1, 2, 3}, 6));

        assertEquals(3, P2J15.countSubarraysWithSum(new int[]{4, 1, 3, 1, 2, 2}, 5));
        assertEquals(4, P2J15.countSubarraysWithSum(new int[]{4, 1, 3, 1, 2, 2}, 4));
        assertEquals(2, P2J15.countSubarraysWithSum(new int[]{4, 1, 3, 1, 2, 2}, 8));
        assertEquals(1, P2J15.countSubarraysWithSum(new int[]{1, 2, 6}, 8));
        assertEquals(1, P2J15.countSubarraysWithSum(new int[]{1, 2, 6}, 9));
        assertEquals(1, P2J15.countSubarraysWithSum(new int[]{1, 2, 6}, 3));
        assertEquals(0, P2J15.countSubarraysWithSum(new int[]{1, 2, 6}, 7));

        // All ones: many overlapping subarrays
        assertEquals(4, P2J15.countSubarraysWithSum(new int[]{1, 1, 1, 1, 1}, 2));
        assertEquals(3, P2J15.countSubarraysWithSum(new int[]{1, 1, 1, 1, 1}, 3));
        assertEquals(1, P2J15.countSubarraysWithSum(new int[]{1, 1, 1, 1, 1}, 5));

        // Entire array sums to target
        assertEquals(1, P2J15.countSubarraysWithSum(new int[]{3, 4, 5}, 12));
    }

    @Test public void testCountSubarraysWithSumCrossValidation() {
        // Cross-validate against brute-force O(n^2) for small inputs
        Random rng = new Random(42);
        for (int trial = 0; trial < 500; trial++) {
            int len = rng.nextInt(20) + 1;
            int[] arr = new int[len];
            for (int j = 0; j < len; j++) arr[j] = 1 + rng.nextInt(5);
            int sum = 2 + rng.nextInt(len * 3);
            int expected = bruteForceCount(arr, sum);
            int actual = P2J15.countSubarraysWithSum(arr, sum);
            assertEquals("Mismatch for " + Arrays.toString(arr) + " sum=" + sum,
                    expected, actual);
        }
    }

    private static int bruteForceCount(int[] arr, int sum) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            int s = 0;
            for (int j = i; j < arr.length; j++) {
                s += arr[j];
                if (s == sum) count++;
                if (s >= sum) break;
            }
        }
        return count;
    }

    @Test public void testCountSubarraysWithSumHundred() {
        testCountSubarraysWithSum(100, 4245028336L);
    }

    @Test public void testCountSubarraysWithSumTenThousand() {
        testCountSubarraysWithSum(10_000, 3489618846L);
    }

    @Test public void testCountSubarraysWithSumThirtyThousand() {
        testCountSubarraysWithSum(30_000, 2990608809L);
    }

    private void testCountSubarraysWithSum(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345 + n);
        int count = 0, goal = 3, m = 7;
        for (int i = 0; i < n; i++) {
            int[] a = new int[m];
            for (int j = 0; j < m; j++) {
                a[j] = 1 + rng.nextInt(2 + i % m);
            }
            for (int j = 0; j < m / 4; j++) {
                int k = rng.nextInt(m - 2);
                int s = a[k] + a[k + 1] + a[k + 2];
                int result = P2J15.countSubarraysWithSum(a, s);
                check.update(result);
            }
            if (++count == goal) {
                count = 0; goal++; m++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}