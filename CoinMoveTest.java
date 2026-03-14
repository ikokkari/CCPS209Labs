import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

public class CoinMoveTest {

    // Helper to build obligations from int arrays
    private static List<List<Integer>> obs(int[]... lists) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] arr : lists) {
            List<Integer> inner = new ArrayList<>();
            for (int v : arr) { inner.add(v); }
            result.add(inner);
        }
        return result;
    }

    // Helper to run coinStep and return the result
    private static int[] step(int[] curr, List<List<Integer>> obligations) {
        int[] next = new int[curr.length];
        CoinMove.coinStep(curr.clone(), next, obligations);
        return next;
    }

    // --- coinStep explicit tests ---

    @Test public void testCoinStepStableState() {
        // Both positions fire to each other: [3,1] -> [3,1] (stable)
        List<List<Integer>> o = obs(new int[]{1}, new int[]{0});
        assertArrayEquals(new int[]{3, 1}, step(new int[]{3, 1}, o));
    }

    @Test public void testCoinStepNotEnoughChips() {
        // Position 0 needs 2 chips to fire but has only 1: nothing happens
        List<List<Integer>> o = obs(new int[]{1, 1}, new int[]{0});
        assertArrayEquals(new int[]{1, 0}, step(new int[]{1, 0}, o));
    }

    @Test public void testCoinStepAllChipsFired() {
        // Position 0 has exactly 2 chips, fires both to position 1
        List<List<Integer>> o = obs(new int[]{1, 1}, new int[]{0});
        assertArrayEquals(new int[]{0, 2}, step(new int[]{2, 0}, o));
    }

    @Test public void testCoinStepCircularThreePositions() {
        // Circular: 0->1, 1->2, 2->0. Only position 0 can fire.
        List<List<Integer>> o = obs(new int[]{1}, new int[]{2}, new int[]{0});
        assertArrayEquals(new int[]{2, 1, 0}, step(new int[]{3, 0, 0}, o));
    }

    @Test public void testCoinStepMultipleObligationsToSameTarget() {
        // Position 0 has 3 obligations to position 1, fires 3 chips
        List<List<Integer>> o = obs(new int[]{1, 1, 1}, new int[]{0});
        assertArrayEquals(new int[]{1, 3}, step(new int[]{4, 0}, o));
    }

    @Test public void testCoinStepBothFire() {
        // Both positions fire simultaneously: [2,2] -> [2,2] stable
        List<List<Integer>> o = obs(new int[]{1}, new int[]{0});
        assertArrayEquals(new int[]{2, 2}, step(new int[]{2, 2}, o));
    }

    @Test public void testCoinStepEmptyObligations() {
        // No obligations: nothing moves
        List<List<Integer>> o = obs(new int[]{}, new int[]{});
        assertArrayEquals(new int[]{5, 3}, step(new int[]{5, 3}, o));
    }

    @Test public void testCoinStepSelfObligation() {
        // Position 0 has obligations to self and to position 1
        List<List<Integer>> o = obs(new int[]{0, 1}, new int[]{});
        assertArrayEquals(new int[]{1, 1}, step(new int[]{2, 0}, o));
    }

    @Test public void testCoinStepChipConservation() {
        // Total chips before and after must be equal
        List<List<Integer>> o = obs(new int[]{1, 2}, new int[]{0, 2}, new int[]{0, 1});
        int[] curr = {5, 3, 2};
        int totalBefore = Arrays.stream(curr).sum();
        int[] result = step(curr, o);
        int totalAfter = Arrays.stream(result).sum();
        assertEquals(totalBefore, totalAfter);
    }

    @Test public void testCoinStepMultiStep() {
        // Trace multiple steps: [3,0,0] -> [2,1,0] -> [1,2,0] -> [1,1,1]
        List<List<Integer>> o = obs(new int[]{1}, new int[]{0, 2}, new int[]{0, 1});
        int[] s = {3, 0, 0};
        s = step(s, o);
        assertArrayEquals(new int[]{2, 1, 0}, s);
        s = step(s, o);
        assertArrayEquals(new int[]{1, 2, 0}, s);
        s = step(s, o);
        assertArrayEquals(new int[]{1, 1, 1}, s);
    }

    // --- period explicit tests ---

    @Test public void testPeriodOne() {
        // Stable state: period 1
        List<List<Integer>> o = obs(new int[]{1}, new int[]{0});
        assertEquals(1, CoinMove.period(new int[]{2, 2}, o));
    }

    @Test public void testPeriodOneNoFiring() {
        // No one can fire: already in terminal state, period 1
        List<List<Integer>> o = obs(new int[]{1}, new int[]{2}, new int[]{0});
        assertEquals(1, CoinMove.period(new int[]{0, 0, 0}, o));
    }

    @Test public void testPeriodTwo() {
        // Classic 2-cycle: chips bounce back and forth
        // [2,0] -> [0,2] -> [2,0] -> ...
        List<List<Integer>> o = obs(new int[]{1, 1}, new int[]{0, 0});
        assertEquals(2, CoinMove.period(new int[]{2, 0}, o));
    }

    @Test public void testPeriodThree() {
        // [1,0,0] obs=[[1],[2],[0]]: single chip circulates -> 3-cycle
        // [1,0,0] -> [0,1,0] -> [0,0,1] -> [1,0,0]
        List<List<Integer>> o = obs(new int[]{1}, new int[]{2}, new int[]{0});
        assertEquals(3, CoinMove.period(new int[]{1, 0, 0}, o));
    }

    @Test public void testPeriodFour() {
        // obs=[[1],[0,2],[0,1]], start=[3,0,0]
        // Enters cycle of length 4 after transient
        List<List<Integer>> o = obs(new int[]{1}, new int[]{0, 2}, new int[]{0, 1});
        assertEquals(4, CoinMove.period(new int[]{3, 0, 0}, o));
    }

    @Test public void testPeriodNine() {
        // obs=[[1],[2,0,0],[1,0,0]], start=[5,0,0]
        List<List<Integer>> o = obs(new int[]{1}, new int[]{2, 0, 0}, new int[]{1, 0, 0});
        assertEquals(9, CoinMove.period(new int[]{5, 0, 0}, o));
    }

    @Test public void testPeriodWithTransient() {
        // Start=[3,0,0] with obs=[[1],[0,2],[0,1]]: takes 2 steps to enter the 4-cycle
        // Steps: [3,0,0] -> [2,1,0] -> [1,2,0] -> cycle starts
        // Period is 4 (the cycle length, not the transient)
        List<List<Integer>> o = obs(new int[]{1}, new int[]{0, 2}, new int[]{0, 1});
        assertEquals(4, CoinMove.period(new int[]{3, 0, 0}, o));
    }

    @Test public void testPeriodStartInCycle() {
        // Start directly in the cycle: [1,1,1] obs=[[1],[0,2],[0,1]]
        // From trace: [1,1,1] -> [0,2,1] -> [1,0,2] -> [1,2,0] -> [1,1,1]
        List<List<Integer>> o = obs(new int[]{1}, new int[]{0, 2}, new int[]{0, 1});
        assertEquals(4, CoinMove.period(new int[]{1, 1, 1}, o));
    }

    @Test public void testPeriodSinglePosition() {
        // Single position with obligation to self: can always fire if chips >= obligations
        // [1] with obs=[[0,0]]: 1 < 2, can't fire. Stable. Period 1.
        List<List<Integer>> o = obs(new int[]{0, 0});
        assertEquals(1, CoinMove.period(new int[]{1}, o));
    }

    // --- CRC mass tests ---

    @Test public void testCoinMove() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        List<List<Integer>> nbs = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            int n = rng.nextInt(i / 3 + 2) + 2;
            int c = rng.nextInt(3 * n + 3) + 2;
            int[] curr = new int[n];
            int[] next = new int[n];
            nbs.clear();
            for(int j = 0; j < n; j++) {
                ArrayList<Integer> nb = new ArrayList<>();
                while(rng.nextInt(100) < 70) {
                    int nn = rng.nextInt(n);
                    if(nn == j) { nn = (nn + 1) % n; }
                    nb.add(nn);
                }
                nbs.add(nb);
            }
            for(int j = 0; j < c; j++) {
                curr[rng.nextInt(n)]++;
            }
            CoinMove.coinStep(curr, next, nbs);
            for(int e: next) { check.update(e); }
        }
        assertEquals(561039959L, check.getValue());
    }

    @Test public void testPeriod() {
        Random rng = new Random(7777);
        CRC32 check = new CRC32();
        List<List<Integer>> nbs = new ArrayList<>();
        for(int i = 0; i < 300; i++) {
            int n = rng.nextInt(15) + 2;
            int c = rng.nextInt(3 * n + 3) + 2;
            int[] curr = new int[n];
            nbs.clear();
            for(int j = 0; j < n; j++) {
                ArrayList<Integer> nb = new ArrayList<>();
                while(nb.size() == 0 || rng.nextInt(100) < 70) {
                    int nn = rng.nextInt(n);
                    if(nn == j) { nn = (nn + 1) % n; }
                    nb.add(nn);
                }
                nbs.add(nb);
            }
            for(int j = 0; j < c; j++) {
                curr[rng.nextInt(n)]++;
            }
            int result = CoinMove.period(curr, nbs);
            check.update(result);
        }
        assertEquals(3003526382L, check.getValue());
    }
}