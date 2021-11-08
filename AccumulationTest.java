import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import java.util.Arrays;

public class AccumulationTest {

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
            // Create new array of random numbers.
            for(int j = 0; j < n; j++) {
                a[j] = rng.nextInt(n*n);
                a[j] *= rng.nextBoolean() ? +1 : -1;
            }
            // Accumulate values.
            int[] accum = Accumulation.accumulate1D(a);
            assertEquals(accum.length, n);
            // Verify that accumulation fits the original values.
            int sum = 0;
            for(int j = 0; j < n; j++) {
                sum += a[j];
                assertEquals(accum[j], sum);
                check.update(accum[j]);
            }
            // Make all possible subarray sum queries.
            for(int j = 0; j < n; j++) {
                for(int k = j; k <= n; k++) {
                    int result = Accumulation.subarraySum(accum, j, k);
                    check.update(result);
                }
            }
            // Increase n when it's time.
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
            // Create a 2D array of random numbers.
            int aw = rng.nextInt(n) + 1;
            int ah = rng.nextInt(n) + 1;
            int[][] a = new int[ah][aw];
            for(int j = 0; j < ah; j++) {
                for(int k = 0; k < aw; k++) {
                    a[j][k] = rng.nextInt(n*n);
                    a[j][k] *= rng.nextBoolean() ? +1 : -1;
                }
            }
            // Compute the accumulation.
            int[][] accum = Accumulation.accumulate2D(a);
            assertEquals(a.length, ah);
            // Perform all possible subrectangle queries. This is O(n^4), so the method
            // subrectangleSum should work very fast to make this finish while we wait.
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
            // Increase n when it's time.
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
            // Create 2D array of truth values.
            int ah = rng.nextInt(n) + 1;
            int aw = rng.nextInt(n) + 1;
            boolean[][] a = new boolean[ah][aw];
            // Decide how many bits to turn on this time.
            int trueCount = 0, block = (ah * aw) / 4 + 1;
            for(int j = 0; j < 4; j++) {
                trueCount += rng.nextInt(block);
            }
            // Fill the array with a bunch of overlapping smallish squares.
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
            // Ask the for the largest square.
            int result = Accumulation.largestTrueSquare(a);
            check.update(result);
            // Increase n when it's time.
            if(++count == goal) {
                count = 0;
                goal += 2;
                ++n;
            }
        }
        assertEquals(2032393799L, check.getValue());
    }
}
