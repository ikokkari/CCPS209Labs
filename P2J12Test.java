import org.junit.Test;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J12Test {

    private static final int[] FOREST_FIRE_EXPECTED = {
            0, 1, 1, 2, 1, 1, 2, 2, 4, 4, 1, 1, 2, 1, 1, 2, 2, 4, 4, 2, 4, 4, 5, 5, 8,
            5, 5, 9, 1, 1, 2, 1, 1, 2, 2, 4, 4, 1, 1, 2, 1, 1, 2, 2, 4, 4, 2, 4, 4,
            5, 5, 8, 5, 5, 9, 9, 4, 4, 5, 5, 10, 5, 5, 10, 2, 10, 13, 11, 10, 8, 11,
            13, 10, 12, 10, 10, 12, 10, 11, 14, 20, 13
    };

    private static final int[] REMY_SIGRIST_EXPECTED = {
            0, 0, 0, 1, 0, 2, 3, 4, 0, 3, 2, 5, 1, 6, 7, 8, 0, 7, 6, 9, 5, 10, 11, 12,
            4, 13, 14, 15, 16, 17, 18, 19, 0, 11, 10, 16, 9, 14, 13, 20, 12, 21, 22, 23,
            24, 25, 26, 27, 1, 28, 29
    };

    private static final int ROUNDS = 10000;

    // ---------------------------------------------------------------
    // forestFire tests
    // ---------------------------------------------------------------

    @Test public void testForestFireExplicit() {
        int[] actual = P2J12.forestFire(FOREST_FIRE_EXPECTED.length - 1);
        assertArrayEquals(FOREST_FIRE_EXPECTED, actual);
    }

    @Test public void testForestFireSmall() {
        // n=1: array is [0, 1]
        int[] r1 = P2J12.forestFire(1);
        assertEquals(2, r1.length);
        assertEquals(0, r1[0]);
        assertEquals(1, r1[1]);

        // n=2: [0, 1, 1]
        int[] r2 = P2J12.forestFire(2);
        assertEquals(3, r2.length);
        assertEquals(0, r2[0]);
        assertEquals(1, r2[1]);
        assertEquals(1, r2[2]);

        // n=3: [0, 1, 1, 2]
        int[] r3 = P2J12.forestFire(3);
        assertEquals(4, r3.length);
        assertEquals(2, r3[3]);
    }

    @Test public void testForestFireNoArithmeticProgression() {
        // Verify the defining property: for no i and positive j does
        // a[i] - a[i-j] == a[i-j] - a[i-2j] hold
        int n = 500;
        int[] a = P2J12.forestFire(n);
        assertEquals(n + 1, a.length);
        assertEquals(0, a[0]);
        for (int i = 1; i <= n; i++) {
            assertTrue("a[" + i + "] must be positive", a[i] >= 1);
            for (int j = 1; i - 2 * j >= 1; j++) {
                int d1 = a[i] - a[i - j];
                int d2 = a[i - j] - a[i - 2 * j];
                assertTrue("Arithmetic progression at i=" + i + " j=" + j +
                                ": a[i]=" + a[i] + " a[i-j]=" + a[i - j] + " a[i-2j]=" + a[i - 2 * j],
                        d1 != d2);
            }
        }
    }

    @Test public void testForestFireGreedy() {
        // Verify greedy: each a[i] is the SMALLEST positive integer that works
        int n = 200;
        int[] a = P2J12.forestFire(n);
        for (int i = 2; i <= n; i++) {
            // For every v < a[i], v must create an arithmetic progression
            for (int v = 1; v < a[i]; v++) {
                boolean createsAP = false;
                for (int j = 1; i - 2 * j >= 1; j++) {
                    if (v - a[i - j] == a[i - j] - a[i - 2 * j]) {
                        createsAP = true;
                        break;
                    }
                }
                assertTrue("v=" + v + " works at i=" + i + " but a[i]=" + a[i],
                        createsAP);
            }
        }
    }

    @Test public void testForestFireFuzz() {
        CRC32 check = new CRC32();
        int[] actual = P2J12.forestFire(ROUNDS);
        for (int e : actual) {
            check.update(e);
        }
        assertEquals(3940994222L, check.getValue());
    }

    // ---------------------------------------------------------------
    // remySigrist tests
    // ---------------------------------------------------------------

    @Test public void testRemySigristExplicit() {
        int[] actual = P2J12.remySigrist(REMY_SIGRIST_EXPECTED.length - 1);
        assertArrayEquals(REMY_SIGRIST_EXPECTED, actual);
    }

    @Test public void testRemySigristSmall() {
        // n=1: [0, 0] (position 0 unused, a[1]=0)
        int[] r1 = P2J12.remySigrist(1);
        assertEquals(2, r1.length);
        assertEquals(0, r1[0]);
        assertEquals(0, r1[1]);

        // n=2: [0, 0, 0]
        int[] r2 = P2J12.remySigrist(2);
        assertEquals(3, r2.length);
        assertEquals(0, r2[2]); // 2 & 1 = 0, so color 0 works

        // n=3: [0, 0, 0, 1] since 3 & 1 != 0 (both have bit 0), so color 0 fails; color 1 works
        int[] r3 = P2J12.remySigrist(3);
        assertEquals(1, r3[3]);
    }

    @Test public void testRemySigristBitInvariant() {
        // Verify the defining property: for every position i with color c,
        // no earlier position j with the same color c has i & j != 0
        int n = 500;
        int[] a = P2J12.remySigrist(n);
        assertEquals(n + 1, a.length);
        assertEquals(0, a[0]);
        for (int i = 1; i <= n; i++) {
            int c = a[i];
            assertTrue("Color must be non-negative", c >= 0);
            for (int j = 1; j < i; j++) {
                if (a[j] == c) {
                    assertEquals("Bit conflict: i=" + i + " j=" + j + " color=" + c +
                            " but i&j=" + (i & j), 0, i & j);
                }
            }
        }
    }

    @Test public void testRemySigristGreedy() {
        // Verify greedy: each a[i] is the SMALLEST color that works
        int n = 200;
        int[] a = P2J12.remySigrist(n);
        for (int i = 2; i <= n; i++) {
            // For every color c < a[i], c must conflict with some earlier position
            for (int c = 0; c < a[i]; c++) {
                boolean conflicts = false;
                for (int j = 1; j < i; j++) {
                    if (a[j] == c && (i & j) != 0) {
                        conflicts = true;
                        break;
                    }
                }
                assertTrue("Color " + c + " works at i=" + i + " but a[i]=" + a[i],
                        conflicts);
            }
        }
    }

    @Test public void testRemySigristFuzz() {
        CRC32 check = new CRC32();
        int[] actual = P2J12.remySigrist(ROUNDS);
        for (int e : actual) {
            check.update(e);
        }
        assertEquals(186819056L, check.getValue());
    }
}