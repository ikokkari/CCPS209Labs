import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class P2J15Test {

    @Test public void testFindClosestElementsExplicit() {
        int[] a0 = {1, 2, 3, 4, 5};
        int[] e0 = {1, 2, 3, 4};
        assertArrayEquals(e0, P2J15.findClosestElements(a0, 3, 4));
        int[] a1 = {1, 2, 8, 10, 11, 13, 22};
        int[] e1 = {8, 10, 11, 13};
        assertArrayEquals(e1, P2J15.findClosestElements(a1, 8, 4));
        int[] a2 = {3, 4, 10, 20, 30, 40};
        int[] e2 = {3, 4, 10};
        assertArrayEquals(e2, P2J15.findClosestElements(a2, 10, 3));
        int[] a3 = {1, 10, 19};
        int[] e3 = {1, 10};
        assertArrayEquals(e3, P2J15.findClosestElements(a3, 10, 2));
        int[] a4 = {4, 5, 10, 11, 22, 33};
        int[] e4 = {11, 22, 33};
        assertArrayEquals(e4, P2J15.findClosestElements(a4, 33, 3));
        int[] a5 = {6, 7, 10, 12, 20, 28};
        int[] e5 = {6};
        assertArrayEquals(e5, P2J15.findClosestElements(a5, 6, 1));
        int[] a6 = {5, 10, 12, 18, 25, 26, 32};
        int[] e6 = {10, 12, 18, 25, 26, 32};
        assertArrayEquals(e6, P2J15.findClosestElements(a6, 25, 6));
        int[] a7 = {1, 2, 4, 11, 15, 23, 25, 33, 43, 47};
        int[] e7 = {4, 11, 15, 23, 25, 33, 43, 47};
        assertArrayEquals(e7, P2J15.findClosestElements(a7, 43, 8));
        int[] a8 = {9, 10, 17, 29, 36, 40, 42, 55, 64, 66, 70, 78, 81, 82};
        int[] e8 = {9, 10, 17, 29, 36, 40, 42, 55};
        assertArrayEquals(e8, P2J15.findClosestElements(a8, 17, 8));
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
        for(int i = 0; i < n; i++) {
            int[] a = new int[m];
            a[0] = rng.nextInt(m);
            for(int j = 1; j < m; j++) {
                a[j] = a[j - 1] + 1 + rng.nextInt(m);
            }
            int idx = rng.nextInt(m);
            int x = a[idx];
            int k = 1 + rng.nextInt(m-1);
            int[] result = P2J15.findClosestElements(a, x, k);
            check.update(result.length);
            for(int j = 0; j < result.length; j++) {
                check.update(result[j]);
            }
            if(++count == goal) {
                count = 0; goal += 2; m++;
            }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testCountSubarraysWithSumExplicit() {
        int[] empty = {};
        assertEquals(0, P2J15.countSubarraysWithSum(empty, 42));
        int[] singleton = {42};
        assertEquals(1, P2J15.countSubarraysWithSum(singleton, 42));
        assertEquals(0, P2J15.countSubarraysWithSum(singleton, 99));
        int[] a0 = {2, 2, 2};
        assertEquals(2, P2J15.countSubarraysWithSum(a0, 4));
        assertEquals(0, P2J15.countSubarraysWithSum(a0, 5));
        assertEquals(1, P2J15.countSubarraysWithSum(a0, 6));
        int[] a1 = {6, 3, 1, 2, 3};
        assertEquals(3, P2J15.countSubarraysWithSum(a1, 6));
        int[] a2 = {4, 1, 3, 1, 2, 2};
        assertEquals(3, P2J15.countSubarraysWithSum(a2, 5));
        assertEquals(4, P2J15.countSubarraysWithSum(a2, 4));
        assertEquals(2, P2J15.countSubarraysWithSum(a2, 8));
        int[] a3 = {1, 2, 6};
        assertEquals(1, P2J15.countSubarraysWithSum(a3, 8));
        assertEquals(1, P2J15.countSubarraysWithSum(a3, 6));
        assertEquals(1, P2J15.countSubarraysWithSum(a3, 9));
        assertEquals(1, P2J15.countSubarraysWithSum(a3, 3));
        assertEquals(0, P2J15.countSubarraysWithSum(a3, 7));
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
        for(int i = 0; i < n; i++) {
            int[] a = new int[m];
            for(int j = 0; j < m; j++) {
                a[j] = 1 + rng.nextInt(2 + i % m);
            }
            for(int j = 0; j < m/4; j++) {
                int k = rng.nextInt(m-2);
                int s = a[k] + a[k+1] + a[k+2];
                int result = P2J15.countSubarraysWithSum(a, s);
                check.update(result);
            }
            if(++count == goal) {
                count = 0; goal++; m++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}