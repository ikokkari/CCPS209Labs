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

    @Test public void testFallingPower() {
        // Explicit test cases
        assertEquals(720, P2J1.fallingPower(10, 3));
        assertEquals(5040, P2J1.fallingPower(7, 7));
        assertEquals(75030638981760L, P2J1.fallingPower(99, 7));
        assertEquals(3024, P2J1.fallingPower(-6, 4));
        assertEquals(0, P2J1.fallingPower(3, 6));
        assertEquals(1, P2J1.fallingPower(42, 0));
        assertEquals(1, P2J1.fallingPower(-98765432, 0));
        assertEquals(-990, P2J1.fallingPower(-9, 3));

        // Mass tester
        CRC32 check = new CRC32();
        for(int b = -10; b < 20; b++) {
            for(int e = 0; e < 10; e++) {
                long p = P2J1.fallingPower(b, e);
                check.update((int)(p & 0xFFFF));
                check.update((int)((p >> 31) & 0xFFFF));
            }
        }
        assertEquals(2652223294L, check.getValue());
        // Test was a success!
    }

    @Test public void testEveryOther() {
        // Explicit test cases 
        int[] a1 = {42, 99, 17, 33};
        int[] b1 = {42, 17};
        assertArrayEquals(b1, P2J1.everyOther(a1));

        int[] a2 = {42, 99, 17};
        int[] b2 = {42, 17};
        assertArrayEquals(b2, P2J1.everyOther(a2));

        int[] a3 = {99};
        int[] b3 = {99};
        assertArrayEquals(b3, P2J1.everyOther(a3));

        int[] a4 = {11, 22, 33, 44, 55, 66, 77, 88, 99};
        int[] b4 = {11, 33, 55, 77, 99};
        assertArrayEquals(b4, P2J1.everyOther(a4));

        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < TRIALS; i++) {
            int len = rng.nextInt(1000);
            int[] a = new int[len];
            for(int j = 0; j < len; j++) {
                a[j] = rng.nextInt(100000);
            }
            int[] b = P2J1.everyOther(a);
            check.update(b.length);
            for(int e: b) { check.update(e); }
        }
        assertEquals(3861208241L, check.getValue());
    }

    @Test public void testCreateZigZag() {
        // Explicit test cases
        int[][] e1 = {{4, 5, 6, 7}, {11, 10, 9, 8}, {12, 13, 14, 15}};
        int[][] r1 = P2J1.createZigZag(3, 4, 4);
        assertEquals(Arrays.deepToString(e1), Arrays.deepToString(r1));

        int[][] e2 = {{1, 2}, {4, 3}, {5, 6}, {8, 7}, {9, 10}};
        int[][] r2 = P2J1.createZigZag(5, 2, 1);
        assertEquals(Arrays.deepToString(e2), Arrays.deepToString(r2));

        int[][] e3 = {{42}, {43}, {44}, {45}};
        int[][] r3 = P2J1.createZigZag(4, 1, 42);
        assertEquals(Arrays.deepToString(e3), Arrays.deepToString(r3));

        int[][] e4 = {{77, 78, 79, 80, 81, 82}};
        int[][] r4 = P2J1.createZigZag(1, 6, 77);
        assertEquals(Arrays.deepToString(e4), Arrays.deepToString(r4));

        int[][] e5 = {{42}};
        int[][] r5 = P2J1.createZigZag(1, 1, 42);
        assertEquals(Arrays.deepToString(e5), Arrays.deepToString(r5));

        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < TRIALS; i++) {
            int rows = rng.nextInt(20) + 1;
            int cols = rng.nextInt(20) + 1;
            int start = rng.nextInt(100);
            int[][] zig = P2J1.createZigZag(rows, cols, start);
            assertEquals(rows, zig.length);
            for(int j = 0; j < rows; j++) {
                assertEquals(cols, zig[j].length);
                for(int e: zig[j]) { check.update(e); }
            }
        }
        assertEquals(3465650385L, check.getValue());
    }

    @Test public void testCountInversions() {
        // Explicit test cases
        int[] a1 = {42, 17, 99, 5};
        assertEquals(4, P2J1.countInversions(a1));

        int[] a2 = {999};
        assertEquals(0, P2J1.countInversions(a2));

        int[] a3 = {77, 44, 33, 22, 0, -11, -12345678};
        assertEquals(21, P2J1.countInversions(a3));

        int[] a4 = {-12345678, -11, 0, 22, 33, 44, 77};
        assertEquals(0, P2J1.countInversions(a4));

        int[] a5 = {3, 5, 1, 7, 0, 9};
        assertEquals(6, P2J1.countInversions(a5));

        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < 1000; i++) {
            int[] a = new int[i];
            for(int j = 0; j < i; j++) {
                a[j] = rng.nextInt(100000);
            }
            check.update(P2J1.countInversions(a));
        }
        assertEquals(1579619806L, check.getValue());
    }
}