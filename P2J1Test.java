import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J1Test {

    private static final int SEED = 12345;
    private static final int TRIALS = 10000;

    // ---------------------------------------------------------------
    // fallingPower tests
    // ---------------------------------------------------------------

    @Test public void testFallingPowerExplicit() {
        // Basic positive base cases
        assertEquals(720, P2J1.fallingPower(10, 3));       // 10*9*8
        assertEquals(5040, P2J1.fallingPower(7, 7));        // 7! = 5040
        assertEquals(30240, P2J1.fallingPower(10, 5));      // 10*9*8*7*6
        assertEquals(75030638981760L, P2J1.fallingPower(99, 7));

        // Negative base cases
        assertEquals(3024, P2J1.fallingPower(-6, 4));       // -6*-7*-8*-9 = 3024
        assertEquals(-990, P2J1.fallingPower(-9, 3));       // -9*-10*-11 = -990
        assertEquals(-6720, P2J1.fallingPower(-4, 5));      // -4*-5*-6*-7*-8 = -6720

        // Exponent zero: any base gives 1
        assertEquals(1, P2J1.fallingPower(42, 0));
        assertEquals(1, P2J1.fallingPower(-98765432, 0));
        assertEquals(1, P2J1.fallingPower(0, 0));           // 0^0 = 1 by convention
        assertEquals(1, P2J1.fallingPower(1, 0));
        assertEquals(1, P2J1.fallingPower(-1, 0));

        // Exponent one: returns the base itself
        assertEquals(5, P2J1.fallingPower(5, 1));
        assertEquals(-3, P2J1.fallingPower(-3, 1));
        assertEquals(0, P2J1.fallingPower(0, 1));

        // Base zero with k >= 1: product includes a zero factor
        assertEquals(0, P2J1.fallingPower(0, 5));

        // Base crosses zero during falling: 3*2*1*0*... = 0
        assertEquals(0, P2J1.fallingPower(3, 6));
        assertEquals(0, P2J1.fallingPower(3, 4));           // 3*2*1*0 = 0
        assertEquals(6, P2J1.fallingPower(3, 3));            // 3*2*1 = 6
        assertEquals(0, P2J1.fallingPower(1, 3));            // 1*0*(-1) = 0

        // Base equals exponent: n! (factorial)
        assertEquals(1, P2J1.fallingPower(1, 1));
        assertEquals(2, P2J1.fallingPower(2, 2));
        assertEquals(120, P2J1.fallingPower(5, 5));
        assertEquals(3628800, P2J1.fallingPower(10, 10));

        // Large result requiring long arithmetic
        assertEquals(858277728000L, P2J1.fallingPower(100, 6)); // 100*99*98*97*96*95
    }

    @Test public void testFallingPowerFuzz() {
        CRC32 check = new CRC32();
        for (int b = -10; b < 20; b++) {
            for (int e = 0; e < 10; e++) {
                long p = P2J1.fallingPower(b, e);
                check.update((int) (p & 0xFFFF));
                check.update((int) ((p >> 31) & 0xFFFF));
            }
        }
        assertEquals(2652223294L, check.getValue());
    }

    @Test public void testFallingPowerFuzzLarge() {
        // Broader fuzz with larger values to stress long arithmetic
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for (int i = 0; i < TRIALS; i++) {
            int base = rng.nextInt(201) - 50;   // -50..150
            int exp = rng.nextInt(13);           // 0..12, kept modest to avoid overflow
            long p = P2J1.fallingPower(base, exp);
            // Verify against a simple reference computation
            long ref = 1;
            long b = base;
            for (int j = 0; j < exp; j++) {
                ref *= b;
                b--;
            }
            assertEquals("fallingPower(" + base + ", " + exp + ")", ref, p);
            check.update((int) (p & 0xFFFF));
            check.update((int) ((p >> 16) & 0xFFFF));
            check.update((int) ((p >> 32) & 0xFFFF));
            check.update((int) ((p >> 48) & 0xFFFF));
        }
        assertEquals(3140707496L, check.getValue());
    }

    // ---------------------------------------------------------------
    // everyOther tests
    // ---------------------------------------------------------------

    @Test public void testEveryOtherExplicit() {
        // Empty array
        assertArrayEquals(new int[]{}, P2J1.everyOther(new int[]{}));

        // Singleton
        assertArrayEquals(new int[]{99}, P2J1.everyOther(new int[]{99}));

        // Two elements (even length)
        assertArrayEquals(new int[]{42}, P2J1.everyOther(new int[]{42, 99}));

        // Odd length
        assertArrayEquals(new int[]{42, 17}, P2J1.everyOther(new int[]{42, 99, 17}));

        // Even length
        assertArrayEquals(new int[]{42, 17}, P2J1.everyOther(new int[]{42, 99, 17, 33}));

        // Longer odd-length array
        assertArrayEquals(new int[]{11, 33, 55, 77, 99},
                P2J1.everyOther(new int[]{11, 22, 33, 44, 55, 66, 77, 88, 99}));

        // Longer even-length array
        assertArrayEquals(new int[]{11, 33, 55, 77},
                P2J1.everyOther(new int[]{11, 22, 33, 44, 55, 66, 77, 88}));

        // All same elements
        assertArrayEquals(new int[]{7, 7, 7}, P2J1.everyOther(new int[]{7, 7, 7, 7, 7}));

        // Negative values
        assertArrayEquals(new int[]{-1, -3}, P2J1.everyOther(new int[]{-1, -2, -3, -4}));
    }

    @Test public void testEveryOtherResultLength() {
        // Verify result length is exactly right for many sizes
        for (int len = 0; len <= 50; len++) {
            int[] a = new int[len];
            for (int j = 0; j < len; j++) a[j] = j;
            int[] result = P2J1.everyOther(a);
            int expectedLen = (len + 1) / 2;
            assertEquals("Wrong result length for input length " + len, expectedLen, result.length);
            // Verify content
            for (int j = 0; j < result.length; j++) {
                assertEquals(2 * j, result[j]);
            }
        }
    }

    @Test public void testEveryOtherFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for (int i = 0; i < TRIALS; i++) {
            int len = rng.nextInt(1000);
            int[] a = new int[len];
            for (int j = 0; j < len; j++) {
                a[j] = rng.nextInt(100000);
            }
            int[] b = P2J1.everyOther(a);
            // Verify length
            assertEquals((len + 1) / 2, b.length);
            // Verify content matches even-indexed positions
            for (int j = 0; j < b.length; j++) {
                assertEquals(a[2 * j], b[j]);
            }
            check.update(b.length);
            for (int e : b) { check.update(e); }
        }
        assertEquals(3861208241L, check.getValue());
    }

    // ---------------------------------------------------------------
    // createZigZag tests
    // ---------------------------------------------------------------

    @Test public void testCreateZigZagExplicit() {
        // Standard case from spec: 3 rows, 4 cols
        int[][] e1 = {{4, 5, 6, 7}, {11, 10, 9, 8}, {12, 13, 14, 15}};
        assertArrayEquals(e1, P2J1.createZigZag(3, 4, 4));

        // Tall narrow grid: 5 rows, 2 cols
        int[][] e2 = {{1, 2}, {4, 3}, {5, 6}, {8, 7}, {9, 10}};
        assertArrayEquals(e2, P2J1.createZigZag(5, 2, 1));

        // Single column: no reversal possible, all rows ascending (trivially)
        int[][] e3 = {{42}, {43}, {44}, {45}};
        assertArrayEquals(e3, P2J1.createZigZag(4, 1, 42));

        // Single row (even row 0, so ascending)
        int[][] e4 = {{77, 78, 79, 80, 81, 82}};
        assertArrayEquals(e4, P2J1.createZigZag(1, 6, 77));

        // 1x1 grid
        int[][] e5 = {{42}};
        assertArrayEquals(e5, P2J1.createZigZag(1, 1, 42));

        // 2x1 grid: row 1 reversed (trivially single element)
        int[][] e6 = {{0}, {1}};
        assertArrayEquals(e6, P2J1.createZigZag(2, 1, 0));

        // Negative start value
        int[][] e7 = {{-3, -2, -1}, {2, 1, 0}, {3, 4, 5}};
        assertArrayEquals(e7, P2J1.createZigZag(3, 3, -3));

        // Two rows to test just one odd row reversal
        int[][] e8 = {{10, 11, 12}, {15, 14, 13}};
        assertArrayEquals(e8, P2J1.createZigZag(2, 3, 10));

        // Wider grid with start=0
        int[][] e9 = {{0, 1, 2, 3, 4}, {9, 8, 7, 6, 5}};
        assertArrayEquals(e9, P2J1.createZigZag(2, 5, 0));
    }

    @Test public void testCreateZigZagDimensions() {
        // Verify array dimensions are exactly right for various sizes
        Random rng = new Random(42);
        for (int i = 0; i < 200; i++) {
            int rows = rng.nextInt(30) + 1;
            int cols = rng.nextInt(30) + 1;
            int start = rng.nextInt(1000) - 500;
            int[][] result = P2J1.createZigZag(rows, cols, start);
            assertEquals("Wrong number of rows", rows, result.length);
            for (int r = 0; r < rows; r++) {
                assertEquals("Wrong number of cols in row " + r, cols, result[r].length);
            }
        }
    }

    @Test public void testCreateZigZagContentProperties() {
        // Verify structural properties: even rows ascending, odd rows descending,
        // and all values present in consecutive sequence
        int rows = 6, cols = 7, start = -5;
        int[][] zig = P2J1.createZigZag(rows, cols, start);
        int val = start;
        for (int r = 0; r < rows; r++) {
            if (r % 2 == 0) {
                // Even row: ascending
                for (int c = 0; c < cols; c++) {
                    assertEquals(val++, zig[r][c]);
                }
            } else {
                // Odd row: descending, so last element is smallest
                for (int c = cols - 1; c >= 0; c--) {
                    assertEquals(val++, zig[r][c]);
                }
            }
        }
    }

    @Test public void testCreateZigZagFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for (int i = 0; i < TRIALS; i++) {
            int rows = rng.nextInt(20) + 1;
            int cols = rng.nextInt(20) + 1;
            int start = rng.nextInt(100);
            int[][] zig = P2J1.createZigZag(rows, cols, start);
            assertEquals(rows, zig.length);
            for (int j = 0; j < rows; j++) {
                assertEquals(cols, zig[j].length);
                for (int e : zig[j]) { check.update(e); }
            }
        }
        assertEquals(3465650385L, check.getValue());
    }

    // ---------------------------------------------------------------
    // countInversions tests
    // ---------------------------------------------------------------

    @Test public void testCountInversionsExplicit() {
        // Empty array: 0 inversions
        assertEquals(0, P2J1.countInversions(new int[]{}));

        // Singleton: 0 inversions
        assertEquals(0, P2J1.countInversions(new int[]{999}));

        // Two elements, sorted: 0 inversions
        assertEquals(0, P2J1.countInversions(new int[]{1, 2}));

        // Two elements, reversed: 1 inversion
        assertEquals(1, P2J1.countInversions(new int[]{2, 1}));

        // Two equal elements: not an inversion (strict >)
        assertEquals(0, P2J1.countInversions(new int[]{5, 5}));

        // All elements equal: 0 inversions
        assertEquals(0, P2J1.countInversions(new int[]{3, 3, 3, 3}));

        // Mixed case from spec
        assertEquals(4, P2J1.countInversions(new int[]{42, 17, 99, 5}));

        // Another mixed case
        assertEquals(6, P2J1.countInversions(new int[]{3, 5, 1, 7, 0, 9}));

        // Fully sorted ascending: 0 inversions
        assertEquals(0, P2J1.countInversions(new int[]{-12345678, -11, 0, 22, 33, 44, 77}));

        // Fully sorted descending: n*(n-1)/2 inversions
        int[] desc7 = {77, 44, 33, 22, 0, -11, -12345678};
        assertEquals(21, P2J1.countInversions(desc7));

        // Array with duplicates: only strict > counts
        // {2, 1, 1}: pairs (0,1):2>1 yes, (0,2):2>1 yes, (1,2):1>1 no => 2
        assertEquals(2, P2J1.countInversions(new int[]{2, 1, 1}));

        // Sorted with one out-of-place element at end
        // {1, 2, 3, 0}: pairs (0,3) (1,3) (2,3) => 3
        assertEquals(3, P2J1.countInversions(new int[]{1, 2, 3, 0}));

        // Verify n*(n-1)/2 formula for descending arrays of various sizes
        for (int n = 0; n <= 20; n++) {
            int[] desc = new int[n];
            for (int j = 0; j < n; j++) desc[j] = n - j;
            assertEquals("Descending array of size " + n,
                    n * (n - 1) / 2, P2J1.countInversions(desc));
        }
    }

    @Test public void testCountInversionsFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for (int i = 0; i < 1000; i++) {
            int[] a = new int[i];
            for (int j = 0; j < i; j++) {
                a[j] = rng.nextInt(100000);
            }
            check.update(P2J1.countInversions(a));
        }
        assertEquals(1579619806L, check.getValue());
    }

    @Test public void testCountInversionsFuzzWithReference() {
        // Cross-validate against a reference O(n^2) implementation
        // Uses smaller range to force frequent duplicates
        Random rng = new Random(9999);
        for (int i = 0; i < 500; i++) {
            int len = rng.nextInt(80);
            int[] a = new int[len];
            for (int j = 0; j < len; j++) {
                a[j] = rng.nextInt(200) - 100;
            }
            int expected = referenceCountInversions(a);
            assertEquals("Inversions mismatch for " + Arrays.toString(a),
                    expected, P2J1.countInversions(a));
        }
    }

    // Reference implementation for cross-validation
    private static int referenceCountInversions(int[] a) {
        int count = 0;
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[i] > a[j]) count++;
            }
        }
        return count;
    }
}