import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultipleWinnerElectionTest {

    // --- Helper to check total seats ---

    private static void assertTotalSeats(int[] result, int seats) {
        int total = 0;
        for (int s : result) { total += s; }
        assertEquals("Total seats allocated must equal seats parameter", seats, total);
    }

    // --- D'Hondt explicit tests ---

    @Test public void testDHondt() {
        int[] v1 = {6, 26, 33};
        int[] r1 = {1, 5, 6};
        assertArrayEquals(r1, MultipleWinnerElection.DHondt(v1, 12));
        int[] v2 = {44, 46, 86};
        int[] r2 = {2, 3, 5};
        assertArrayEquals(r2, MultipleWinnerElection.DHondt(v2, 10));
        int[] v3 = {130, 19, 31, 77};
        int[] r3 = {12, 1, 3, 7};
        assertArrayEquals(r3, MultipleWinnerElection.DHondt(v3, 23));
    }

    @Test public void testDHondtSpecExample() {
        // The example from the lab specification
        int[] v = {23, 26, 115, 128};
        assertArrayEquals(new int[]{1, 2, 8, 10}, MultipleWinnerElection.DHondt(v, 21));
    }

    @Test public void testDHondtTieBreaking() {
        // Two equal parties, even seats: split evenly
        assertArrayEquals(new int[]{2, 2}, MultipleWinnerElection.DHondt(new int[]{100, 100}, 4));

        // Two equal parties, odd seats: extra seat goes to higher-numbered party
        assertArrayEquals(new int[]{2, 3}, MultipleWinnerElection.DHondt(new int[]{100, 100}, 5));

        // Three equal parties, not divisible by 3: highest-numbered gets the extra
        assertArrayEquals(new int[]{2, 2, 2}, MultipleWinnerElection.DHondt(new int[]{100, 100, 100}, 6));
        assertArrayEquals(new int[]{2, 2, 3}, MultipleWinnerElection.DHondt(new int[]{100, 100, 100}, 7));

        // Single seat, two parties tied: higher-numbered wins
        assertArrayEquals(new int[]{0, 1}, MultipleWinnerElection.DHondt(new int[]{30, 30}, 1));
    }

    @Test public void testDHondtSingleParty() {
        // Only one party: gets all seats
        assertArrayEquals(new int[]{7}, MultipleWinnerElection.DHondt(new int[]{50}, 7));
        assertArrayEquals(new int[]{1}, MultipleWinnerElection.DHondt(new int[]{1}, 1));
    }

    @Test public void testDHondtZeroVotes() {
        // Party with zero votes gets nothing
        assertArrayEquals(new int[]{0, 5}, MultipleWinnerElection.DHondt(new int[]{0, 100}, 5));

        // All parties zero except one: that one gets everything
        assertArrayEquals(new int[]{0, 0, 0, 10}, MultipleWinnerElection.DHondt(new int[]{0, 0, 0, 50}, 10));
    }

    @Test public void testDHondtOneSeat() {
        // Single seat goes to largest party
        assertArrayEquals(new int[]{1, 0, 0}, MultipleWinnerElection.DHondt(new int[]{30, 20, 10}, 1));

        // Single seat, tie between two largest: higher-numbered wins
        assertArrayEquals(new int[]{0, 1, 0}, MultipleWinnerElection.DHondt(new int[]{30, 30, 10}, 1));
    }

    @Test public void testDHondtManyPartiesFewSeats() {
        // More parties than seats: only the top parties get seats
        assertArrayEquals(new int[]{0, 0, 0, 1, 1},
                MultipleWinnerElection.DHondt(new int[]{10, 20, 30, 40, 50}, 2));
        assertArrayEquals(new int[]{0, 0, 1, 1, 1},
                MultipleWinnerElection.DHondt(new int[]{10, 20, 30, 40, 50}, 3));
    }

    @Test public void testDHondtExtremeDisparity() {
        // Tiny party vs dominant party
        int[] result = MultipleWinnerElection.DHondt(new int[]{1, 1000000}, 10);
        assertArrayEquals(new int[]{0, 10}, result);
    }

    // --- Webster explicit tests ---

    @Test public void testWebster() {
        int[] v1 = {6, 26, 33};
        int[] r1 = {1, 5, 6};
        assertArrayEquals(r1, MultipleWinnerElection.webster(v1, 12));
        int[] v2 = {44, 46, 86};
        int[] r2 = {2, 3, 5};
        assertArrayEquals(r2, MultipleWinnerElection.webster(v2, 10));
        int[] v3 = {130, 19, 31, 77};
        int[] r3 = {11, 2, 3, 7};
        assertArrayEquals(r3, MultipleWinnerElection.webster(v3, 23));
    }

    @Test public void testWebsterSpecExample() {
        assertArrayEquals(new int[]{2, 2, 8, 9}, MultipleWinnerElection.webster(new int[]{23, 26, 115, 128}, 21));
    }

    @Test public void testWebsterTieBreaking() {
        assertArrayEquals(new int[]{2, 3}, MultipleWinnerElection.webster(new int[]{100, 100}, 5));
        assertArrayEquals(new int[]{2, 2, 3}, MultipleWinnerElection.webster(new int[]{100, 100, 100}, 7));
    }

    @Test public void testWebsterSingleParty() {
        assertArrayEquals(new int[]{7}, MultipleWinnerElection.webster(new int[]{50}, 7));
    }

    @Test public void testWebsterZeroVotes() {
        assertArrayEquals(new int[]{0, 5}, MultipleWinnerElection.webster(new int[]{0, 100}, 5));
    }

    @Test public void testWebsterFavoursSmallParties() {
        // Webster is more favourable to small parties than D'Hondt.
        // votes = [10, 50, 100], 10 seats:
        // D'Hondt: [0, 3, 7]   Webster: [1, 3, 6]
        int[] v = {10, 50, 100};
        int[] dh = MultipleWinnerElection.DHondt(v, 10);
        int[] ws = MultipleWinnerElection.webster(v, 10);
        assertArrayEquals(new int[]{0, 3, 7}, dh);
        assertArrayEquals(new int[]{1, 3, 6}, ws);
        // Small party (index 0) gets more seats under Webster
        assertTrue(ws[0] >= dh[0]);
    }

    // --- Imperiali explicit tests ---

    @Test public void testImperiali() {
        int[] v1 = {6, 26, 33};
        int[] r1 = {0, 5, 7};
        assertArrayEquals(r1, MultipleWinnerElection.imperiali(v1, 12));
        int[] v2 = {44, 46, 86};
        int[] r2 = {2, 2, 6};
        assertArrayEquals(r2, MultipleWinnerElection.imperiali(v2, 10));
        int[] v3 = {130, 19, 31, 77};
        int[] r3 = {13, 1, 2, 7};
        assertArrayEquals(r3, MultipleWinnerElection.imperiali(v3, 23));
    }

    @Test public void testImperialiSpecExample() {
        assertArrayEquals(new int[]{1, 1, 9, 10}, MultipleWinnerElection.imperiali(new int[]{23, 26, 115, 128}, 21));
    }

    @Test public void testImperialiTieBreaking() {
        assertArrayEquals(new int[]{2, 3}, MultipleWinnerElection.imperiali(new int[]{100, 100}, 5));
        assertArrayEquals(new int[]{2, 2, 3}, MultipleWinnerElection.imperiali(new int[]{100, 100, 100}, 7));
    }

    @Test public void testImperialiSingleParty() {
        assertArrayEquals(new int[]{7}, MultipleWinnerElection.imperiali(new int[]{50}, 7));
    }

    @Test public void testImperialiZeroVotes() {
        assertArrayEquals(new int[]{0, 5}, MultipleWinnerElection.imperiali(new int[]{0, 100}, 5));
    }

    @Test public void testImperialiFavoursLargeParties() {
        // Imperiali is even more favourable to large parties than D'Hondt.
        // votes = [10, 50, 100], 20 seats:
        // D'Hondt: [1, 6, 13]   Imperiali: [0, 6, 14]
        int[] v = {10, 50, 100};
        int[] dh = MultipleWinnerElection.DHondt(v, 20);
        int[] im = MultipleWinnerElection.imperiali(v, 20);
        assertArrayEquals(new int[]{1, 6, 13}, dh);
        assertArrayEquals(new int[]{0, 6, 14}, im);
        // Largest party (index 2) gets more under Imperiali
        assertTrue(im[2] >= dh[2]);
    }

    // --- Cross-method comparison ---

    @Test public void testMethodComparison() {
        // All three methods on the same input; total seats must always match.
        int[] v = {10, 50, 100};
        for (int seats : new int[]{5, 10, 20}) {
            int[] dh = MultipleWinnerElection.DHondt(v, seats);
            int[] ws = MultipleWinnerElection.webster(v, seats);
            int[] im = MultipleWinnerElection.imperiali(v, seats);
            assertTotalSeats(dh, seats);
            assertTotalSeats(ws, seats);
            assertTotalSeats(im, seats);
        }

        // 5 seats: D=[0,1,4] W=[0,2,3] I=[0,1,4]
        assertArrayEquals(new int[]{0, 1, 4}, MultipleWinnerElection.DHondt(v, 5));
        assertArrayEquals(new int[]{0, 2, 3}, MultipleWinnerElection.webster(v, 5));
        assertArrayEquals(new int[]{0, 1, 4}, MultipleWinnerElection.imperiali(v, 5));

        // 10 seats: D=[0,3,7] W=[1,3,6] I=[0,3,7]
        assertArrayEquals(new int[]{0, 3, 7}, MultipleWinnerElection.DHondt(v, 10));
        assertArrayEquals(new int[]{1, 3, 6}, MultipleWinnerElection.webster(v, 10));
        assertArrayEquals(new int[]{0, 3, 7}, MultipleWinnerElection.imperiali(v, 10));

        // 20 seats: D=[1,6,13] W=[1,6,13] I=[0,6,14]
        assertArrayEquals(new int[]{1, 6, 13}, MultipleWinnerElection.DHondt(v, 20));
        assertArrayEquals(new int[]{1, 6, 13}, MultipleWinnerElection.webster(v, 20));
        assertArrayEquals(new int[]{0, 6, 14}, MultipleWinnerElection.imperiali(v, 20));
    }

    // --- CRC mass tests ---

    @Test public void testDHondtHundred() {
        test(100, 3816530178L, 0);
    }

    @Test public void testDHondtTenThousand() {
        test(10000, 572303332L, 0);
    }

    @Test public void testWebsterHundred() {
        test(100, 2959731999L, 1);
    }

    @Test public void testWebsterTenThousand() {
        test(10000, 2360920111L, 1);
    }

    @Test public void testImperialiHundred() {
        test(100, 2278224169L, 2);
    }

    @Test public void testImperialiTenThousand() {
        test(10000, 2082193870L, 2);
    }

    private boolean isSorted(int[] a) {
        int prev = a[0] - 1;
        for(int e : a) {
            if(e < prev) { return false; }
            prev = e;
        }
        return true;
    }

    private void test(int n, long expected, int mode) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int C = 2 + Math.min(i / 20, 20) + Math.min(i / 1000, 20);
            int[] votes = new int[C];
            int seats = 10 + rng.nextInt(4 * C);
            for(int j = 0; j < C; j++) {
                votes[j] += rng.nextInt(i + 10);
                int c = rng.nextInt(C);
                votes[c] += rng.nextInt(2 * i + 1);
            }
            Arrays.sort(votes);
            int[] result0 = null, result1 = null, result2 = null;
            if(mode == 0 || mode == 3) { // DHondt
                result0 = MultipleWinnerElection.DHondt(votes, seats);
                assertTrue(isSorted(result0));
                try {
                    check.update(Arrays.toString(result0).getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) { }
            }
            if(mode == 1 || mode == 3) { // Webster
                result1 = MultipleWinnerElection.webster(votes, seats);
                assertTrue(isSorted(result1));
                try {
                    check.update(Arrays.toString(result1).getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) { }
            }
            if(mode == 2 || mode == 3) { // Imperiali
                result2 = MultipleWinnerElection.imperiali(votes, seats);
                assertTrue(isSorted(result2));
                try {
                    check.update(Arrays.toString(result2).getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) { }
            }
        }
        assertEquals(expected, check.getValue());
    }
}