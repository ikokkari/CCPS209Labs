import org.junit.Test;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class P2J9Test {

    // ---------------------------------------------------------------
    // sumOfTwoDistinctSquares tests
    // ---------------------------------------------------------------

    @Test public void testSumOfTwoDistinctSquaresExplicit() {
        int[] workingStates = {5, 10, 13, 17, 20, 25, 26, 29, 34, 37, 40, 41, 45, 50, 52};
        boolean[] actual = P2J9.sumOfTwoDistinctSquares(53);
        int pos = 0;
        for (int i = 0; i < 53; i++) {
            if (actual[i]) {
                assertEquals(i, workingStates[pos++]);
            }
        }
        assertEquals("Not all working states found", workingStates.length, pos);
    }

    @Test public void testSumOfTwoDistinctSquaresSmall() {
        // n=0: empty array
        boolean[] r0 = P2J9.sumOfTwoDistinctSquares(0);
        assertEquals(0, r0.length);

        // n=1: just index 0, which is false
        boolean[] r1 = P2J9.sumOfTwoDistinctSquares(1);
        assertEquals(1, r1.length);
        assertFalse(r1[0]);

        // n=6: only 5 (=1+4) is true among 0..5
        boolean[] r6 = P2J9.sumOfTwoDistinctSquares(6);
        assertFalse(r6[0]); // 0
        assertFalse(r6[1]); // 1 = 0+1, but a,b must be positive
        assertFalse(r6[2]); // 2 = 1+1 but a must differ from b
        assertFalse(r6[3]);
        assertFalse(r6[4]); // 4 = 0+4, but a,b must be positive
        assertTrue(r6[5]);  // 5 = 1+4 = 1^2 + 2^2
    }

    @Test public void testSumOfTwoDistinctSquaresWitness() {
        // For every true entry, verify a witness a!=b with a^2+b^2=i exists
        // For every false entry, verify no such witness exists
        int n = 500;
        boolean[] result = P2J9.sumOfTwoDistinctSquares(n);
        for (int i = 0; i < n; i++) {
            boolean found = false;
            for (int a = 2; a * a < i && !found; a++) {
                for (int b = 1; b < a; b++) {
                    if (a * a + b * b == i) {
                        found = true;
                        break;
                    }
                    if (a * a + b * b > i) break;
                }
            }
            assertEquals("Mismatch at i=" + i, found, result[i]);
        }
    }

    @Test public void testSumOfTwoDistinctSquaresThousand() {
        test(1000, 4110419952L, 1);
    }

    @Test public void testSumOfTwoDistinctSquaresMillion() {
        test(1_000_000, 2362619161L, 1);
    }

    // ---------------------------------------------------------------
    // subtractSquare tests
    // ---------------------------------------------------------------

    @Test public void testSubtractSquareExplicit() {
        int[] coldStates = {0, 2, 5, 7, 10, 12, 15, 17, 20, 22, 34, 39};
        boolean[] actual = P2J9.subtractSquare(40);
        int pos = 0;
        for (int i = 0; i < 40; i++) {
            if (!actual[i]) {
                assertEquals(i, coldStates[pos++]);
            }
        }
        assertEquals("Not all cold states found", coldStates.length, pos);
    }

    @Test public void testSubtractSquareSmall() {
        // n=0: empty array
        boolean[] r0 = P2J9.subtractSquare(0);
        assertEquals(0, r0.length);

        // n=1: just index 0, which is cold (false)
        boolean[] r1 = P2J9.subtractSquare(1);
        assertEquals(1, r1.length);
        assertFalse(r1[0]); // 0 is cold

        // n=3: 0=cold, 1=hot (move to 0), 2=cold (only move to 1 which is hot)
        boolean[] r3 = P2J9.subtractSquare(3);
        assertFalse(r3[0]);
        assertTrue(r3[1]);
        assertFalse(r3[2]);
    }

    @Test public void testSubtractSquareGameTheory() {
        // Verify the game-theoretic definition:
        // hot = exists move to a cold state
        // cold = all moves lead to hot states (or no moves exist)
        int n = 300;
        boolean[] result = P2J9.subtractSquare(n);
        for (int i = 0; i < n; i++) {
            boolean hasMoveToCold = false;
            for (int j = 1; j * j <= i; j++) {
                if (!result[i - j * j]) {
                    hasMoveToCold = true;
                    break;
                }
            }
            if (result[i]) {
                assertTrue("State " + i + " is hot but has no move to cold", hasMoveToCold);
            } else {
                assertFalse("State " + i + " is cold but has move to cold", hasMoveToCold);
            }
        }
    }

    @Test public void testSubtractSquareThousand() {
        test(1000, 4122798422L, 0);
    }

    @Test public void testSubtractSquareMillion() {
        test(1_000_000, 1504185187L, 0);
    }

    @Test public void testSubtractSquareTenMillion() {
        test(10_000_000, 3315207453L, 0);
    }

    // Same test harness for both methods.
    private void test(int n, long expected, int mode) {
        CRC32 check = new CRC32();
        boolean[] result;
        if (mode == 0) {
            result = P2J9.subtractSquare(n);
        } else {
            result = P2J9.sumOfTwoDistinctSquares(n);
        }
        for (int i = 0; i < n; i++) {
            check.update(result[i] ? i : 0);
        }
        assertEquals(expected, check.getValue());
    }
}