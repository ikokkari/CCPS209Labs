import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import java.util.Arrays;

public class AccumulationTest {

    // --- accumulate1D / subarraySum explicit tests ---

    @Test public void test1DSingleElement() {
        assertArrayEquals(new int[]{5}, Accumulation.accumulate1D(new int[]{5}));
        assertEquals(5, Accumulation.subarraySum(new int[]{5}, 0, 1));
        assertEquals(0, Accumulation.subarraySum(new int[]{5}, 0, 0)); // empty subarray
    }

    @Test public void test1DAllZeros() {
        assertArrayEquals(new int[]{0, 0, 0}, Accumulation.accumulate1D(new int[]{0, 0, 0}));
        assertEquals(0, Accumulation.subarraySum(new int[]{0, 0, 0}, 0, 3));
    }

    @Test public void test1DNegativeValues() {
        int[] acc = Accumulation.accumulate1D(new int[]{-1, -2, -3});
        assertArrayEquals(new int[]{-1, -3, -6}, acc);
        assertEquals(-6, Accumulation.subarraySum(acc, 0, 3));
        assertEquals(-5, Accumulation.subarraySum(acc, 1, 3));
    }

    @Test public void test1DSingletonSubarrays() {
        // Each singleton subarray sum should equal the original element
        int[] orig = {4, -1, 3, 7};
        int[] acc = Accumulation.accumulate1D(orig);
        assertArrayEquals(new int[]{4, 3, 6, 13}, acc);
        for (int i = 0; i < 4; i++) {
            assertEquals(orig[i], Accumulation.subarraySum(acc, i, i + 1));
        }
    }

    @Test public void test1DEmptySubarrayAnywhere() {
        int[] acc = {4, 3, 6, 13};
        // Empty subarrays at every position should be 0
        for (int i = 0; i <= 4; i++) {
            assertEquals(0, Accumulation.subarraySum(acc, i, i));
        }
    }

    @Test public void test1DFullArraySum() {
        int[] acc = Accumulation.accumulate1D(new int[]{4, -1, 3, 7});
        assertEquals(13, Accumulation.subarraySum(acc, 0, 4));
    }

    @Test public void test1DAllSameValues() {
        int[] acc = Accumulation.accumulate1D(new int[]{3, 3, 3, 3, 3});
        assertArrayEquals(new int[]{3, 6, 9, 12, 15}, acc);
        // Any subarray of length k should sum to 3k
        assertEquals(6, Accumulation.subarraySum(acc, 1, 3));
        assertEquals(9, Accumulation.subarraySum(acc, 2, 5));
    }

    // --- accumulate2D / subrectangleSum explicit tests ---

    @Test public void test2DSingleCell() {
        int[][] acc = Accumulation.accumulate2D(new int[][]{{7}});
        assertTrue(Arrays.deepEquals(new int[][]{{7}}, acc));
        assertEquals(7, Accumulation.subrectangleSum(acc, 0, 0, 1, 1));
    }

    @Test public void test2DSingleRow() {
        // Single row should behave like 1D
        int[][] acc = Accumulation.accumulate2D(new int[][]{{1, 2, 3}});
        assertTrue(Arrays.deepEquals(new int[][]{{1, 3, 6}}, acc));
        assertEquals(6, Accumulation.subrectangleSum(acc, 0, 0, 1, 3));
        assertEquals(5, Accumulation.subrectangleSum(acc, 0, 1, 1, 2));
    }

    @Test public void test2DSingleColumn() {
        int[][] acc = Accumulation.accumulate2D(new int[][]{{1}, {2}, {3}});
        assertTrue(Arrays.deepEquals(new int[][]{{1}, {3}, {6}}, acc));
        assertEquals(6, Accumulation.subrectangleSum(acc, 0, 0, 3, 1));
        assertEquals(5, Accumulation.subrectangleSum(acc, 1, 0, 2, 1));
    }

    @Test public void test2DThreeByThree() {
        int[][] grid = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] acc = Accumulation.accumulate2D(grid);
        assertTrue(Arrays.deepEquals(new int[][]{{1, 3, 6}, {5, 12, 21}, {12, 27, 45}}, acc));

        // Full grid sum = 45
        assertEquals(45, Accumulation.subrectangleSum(acc, 0, 0, 3, 3));
        // Bottom-right 2x2: 5+6+8+9 = 28
        assertEquals(28, Accumulation.subrectangleSum(acc, 1, 1, 2, 2));
        // Single element grid[1][1] = 5
        assertEquals(5, Accumulation.subrectangleSum(acc, 1, 1, 1, 1));
        // Top row
        assertEquals(6, Accumulation.subrectangleSum(acc, 0, 0, 1, 3));
        // Left column
        assertEquals(12, Accumulation.subrectangleSum(acc, 0, 0, 3, 1));
        // Middle column
        assertEquals(15, Accumulation.subrectangleSum(acc, 0, 1, 3, 1));
    }

    @Test public void test2DNegativeValues() {
        int[][] grid = {{1, -1}, {-2, 2}, {3, -3}};
        int[][] acc = Accumulation.accumulate2D(grid);
        assertTrue(Arrays.deepEquals(new int[][]{{1, 0}, {-1, 0}, {2, 0}}, acc));
        // Full sum = 0
        assertEquals(0, Accumulation.subrectangleSum(acc, 0, 0, 3, 2));
    }

    // --- largestTrueSquare explicit tests ---

    @Test public void testLargestTrueSquareAllTrue() {
        // Square grid: answer = side length
        assertEquals(3, Accumulation.largestTrueSquare(new boolean[][]{
                {true, true, true}, {true, true, true}, {true, true, true}}));
        // Non-square: answer = min(h, w)
        assertEquals(2, Accumulation.largestTrueSquare(new boolean[][]{
                {true, true, true, true}, {true, true, true, true}}));
    }

    @Test public void testLargestTrueSquareAllFalse() {
        assertEquals(0, Accumulation.largestTrueSquare(new boolean[][]{
                {false, false, false}, {false, false, false}, {false, false, false}}));
    }

    @Test public void testLargestTrueSquareSingleCell() {
        assertEquals(1, Accumulation.largestTrueSquare(new boolean[][]{{true}}));
        assertEquals(0, Accumulation.largestTrueSquare(new boolean[][]{{false}}));
    }

    @Test public void testLargestTrueSquareSingleTrueCell() {
        assertEquals(1, Accumulation.largestTrueSquare(new boolean[][]{
                {false, false}, {false, true}}));
    }

    @Test public void testLargestTrueSquareLShape() {
        // L-shape: can't form a 2x2 square
        assertEquals(1, Accumulation.largestTrueSquare(new boolean[][]{
                {true, true, false}, {true, false, false}, {false, false, false}}));
    }

    @Test public void testLargestTrueSquareCornerBlock() {
        // 2x2 block in top-left corner
        assertEquals(2, Accumulation.largestTrueSquare(new boolean[][]{
                {true, true, false}, {true, true, false}, {false, false, false}}));
    }

    @Test public void testLargestTrueSquareBlockInLargerGrid() {
        // 3x3 true block in top-left of 4x4 grid
        assertEquals(3, Accumulation.largestTrueSquare(new boolean[][]{
                {true, true, true, false}, {true, true, true, false},
                {true, true, true, false}, {false, false, false, false}}));
    }

    @Test public void testLargestTrueSquareCheckerboard() {
        // Checkerboard: no two adjacent true cells share a 2x2 square
        assertEquals(1, Accumulation.largestTrueSquare(new boolean[][]{
                {true, false, true}, {false, true, false}, {true, false, true}}));
    }

    @Test public void testLargestTrueSquareNarrowGrids() {
        // Tall narrow: 5x1, can't form anything bigger than 1x1
        assertEquals(1, Accumulation.largestTrueSquare(new boolean[][]{
                {true}, {true}, {true}, {true}, {true}}));
        // Wide: 1x5
        assertEquals(1, Accumulation.largestTrueSquare(new boolean[][]{
                {true, true, true, true, true}}));
    }

    @Test public void testLargestTrueSquareBlockNotAtOrigin() {
        // 2x2 block in bottom-right, not at origin
        assertEquals(2, Accumulation.largestTrueSquare(new boolean[][]{
                {false, false, false}, {false, true, true}, {false, true, true}}));
    }

    // --- CRC mass tests ---

    @Test public void test1D() {
        // Explicit test cases
        int[] orig1 = {4, -1, 3, 7};
        int[] acc1 = {4, 3, 6, 13};
        assertArrayEquals(acc1, Accumulation.accumulate1D(orig1));
        assertEquals(3, Accumulation.subarraySum(acc1, 0, 2));
        assertEquals(2, Accumulation.subarraySum(acc1, 1, 3));
        assertEquals(13, Accumulation.subarraySum(acc1, 0, 4));
        assertEquals(0, Accumulation.subarraySum(acc1, 2, 2));

        int[] orig2 = {3, 2, -5, 4};
        int[] acc2 = {3, 5, 0, 4};
        assertArrayEquals(acc2, Accumulation.accumulate1D(orig2));
        assertEquals(4, Accumulation.subarraySum(acc2, 0, 4));
        assertEquals(0, Accumulation.subarraySum(acc2, 0, 3));
        assertEquals(-3, Accumulation.subarraySum(acc2, 1, 3));
        assertEquals(0, Accumulation.subarraySum(acc2, 3, 3));

        // Pseudorandom fuzz testing
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        int count = 0, goal = 5, n = 3;
        int[] a = new int[n];
        for(int i = 0; i < 1000; i++) {
            for(int j = 0; j < n; j++) {
                a[j] = rng.nextInt(n*n);
                a[j] *= rng.nextBoolean() ? +1 : -1;
            }
            int[] accum = Accumulation.accumulate1D(a);
            assertEquals(accum.length, n);
            int sum = 0;
            for(int j = 0; j < n; j++) {
                sum += a[j];
                assertEquals(accum[j], sum);
                check.update(accum[j]);
            }
            for(int j = 0; j < n; j++) {
                for(int k = j; k <= n; k++) {
                    int result = Accumulation.subarraySum(accum, j, k);
                    check.update(result);
                }
            }
            if(++count == goal) {
                count = 0;
                goal += 2;
                a = new int[++n];
            }
        }
        assertEquals(160402440L, check.getValue());
    }

    @Test public void test2D() {
        // Explicit test cases
        int[][] orig1 =  {{1, 2, 3}, {4, 5, 6}};
        int[][] accum1 = {{1, 3, 6}, {5, 12, 21}};
        assertTrue(Arrays.deepEquals(accum1, Accumulation.accumulate2D(orig1)));
        assertEquals(5, Accumulation.subrectangleSum(accum1, 0, 0, 2, 1));
        assertEquals(21, Accumulation.subrectangleSum(accum1, 0, 0, 2, 3));
        assertEquals(9, Accumulation.subrectangleSum(accum1, 0, 2, 2, 1));
        assertEquals(16, Accumulation.subrectangleSum(accum1, 0, 1, 2, 2));
        assertEquals(0, Accumulation.subrectangleSum(accum1, 1, 0, 0, 2));

        int[][] orig2 =  {{1, -1}, {-2, 2}, {3, -3}, {-4, 4}, {5, -5}};
        int[][] accum2 = {{1, 0}, {-1, 0}, {2, 0}, {-2, 0}, {3, 0}};
        assertTrue(Arrays.deepEquals(accum2, Accumulation.accumulate2D(orig2)));
        assertEquals(0, Accumulation.subrectangleSum(accum2, 0, 0, 5, 2));
        assertEquals(1, Accumulation.subrectangleSum(accum2, 0, 1, 2, 1));
        assertEquals(3, Accumulation.subrectangleSum(accum2, 1, 1, 3, 1));

        // Pseudorandom fuzz testing
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        int count = 0, goal = 5, n = 3;
        for(int i = 0; i < 20; i++) {
            int aw = rng.nextInt(n) + 1;
            int ah = rng.nextInt(n) + 1;
            int[][] a = new int[ah][aw];
            for(int j = 0; j < ah; j++) {
                for(int k = 0; k < aw; k++) {
                    a[j][k] = rng.nextInt(n*n);
                    a[j][k] *= rng.nextBoolean() ? +1 : -1;
                }
            }
            int[][] accum = Accumulation.accumulate2D(a);
            assertEquals(a.length, ah);
            for(int row = 0; row < ah; row++) {
                assertEquals(accum[row].length, aw);
                for(int col = 0; col < aw; col++) {
                    for(int h = 1; row + h < ah; h++) {
                        for(int w = 1; col + w < aw; w++) {
                            int result = Accumulation.subrectangleSum(accum, row, col, h, w);
                            check.update(result);
                        }
                    }
                }
            }
            if(++count == goal) {
                count = 0;
                goal += 2;
                ++n;
            }
        }
        assertEquals(2200424722L, check.getValue());
    }

    @Test public void testLargestTrueSquare() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        int count = 0, goal = 5, n = 3;
        for(int i = 0; i < 7000; i++) {
            int ah = rng.nextInt(n) + 1;
            int aw = rng.nextInt(n) + 1;
            boolean[][] a = new boolean[ah][aw];
            int trueCount = 0, block = (ah * aw) / 4 + 1;
            for(int j = 0; j < 4; j++) {
                trueCount += rng.nextInt(block);
            }
            while(trueCount > 0) {
                int s = 1;
                while(s < n && rng.nextInt(100) < 50 + (i % 30)) { s++; }
                int row = rng.nextInt(ah);
                int col = rng.nextInt(aw);
                for(int h = 0; h < s; h++) {
                    for(int w = 0; w < s; w++) {
                        int x = (row + h) % ah;
                        int y = (col + w) % aw;
                        if(!a[x][y]) {
                            a[x][y] = true;
                            trueCount--;
                        }
                    }
                }
            }
            int result = Accumulation.largestTrueSquare(a);
            check.update(result);
            if(++count == goal) {
                count = 0;
                goal += 2;
                ++n;
            }
        }
        assertEquals(2032393799L, check.getValue());
    }
}