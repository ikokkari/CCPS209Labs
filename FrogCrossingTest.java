import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

public class FrogCrossingTest {

    // --- Explicit tests ---

    @Test public void testSpecExample() {
        // The example from the problem specification.
        assertEquals(2, FrogCrossing.maximumFrogs(
                new int[]{7, 5, 4}, new int[]{0, 3, 4, 6, 8, 10, 11}));
    }

    @Test public void testOneFrogExactStrength() {
        // Single frog whose strength exactly equals the canyon width.
        assertEquals(1, FrogCrossing.maximumFrogs(
                new int[]{10}, new int[]{0, 3, 5, 10}));
    }

    @Test public void testOneFrogTooWeak() {
        // Single frog cannot bridge the gap from 0 to 10 with no intermediate help.
        assertEquals(0, FrogCrossing.maximumFrogs(
                new int[]{4}, new int[]{0, 10}));
    }

    @Test public void testAllFrogsCross() {
        // All three frogs are strong enough to share the boxes and cross.
        assertEquals(3, FrogCrossing.maximumFrogs(
                new int[]{8, 7, 5}, new int[]{0, 2, 4, 7, 9, 10, 13, 14, 16}));
    }

    @Test public void testNoFrogsCross() {
        // Two frogs, neither can cross the single large gap.
        assertEquals(0, FrogCrossing.maximumFrogs(
                new int[]{3, 2}, new int[]{0, 10}));
    }

    @Test public void testOneFrogJustMakeIt() {
        // One frog can barely hop across using every stepping stone.
        assertEquals(1, FrogCrossing.maximumFrogs(
                new int[]{2}, new int[]{0, 2, 4, 6, 8, 10}));
    }

    @Test public void testTwoFrogsCompeteForBoxes() {
        // Boxes are just barely enough for one frog, not two.
        assertEquals(1, FrogCrossing.maximumFrogs(
                new int[]{3, 3}, new int[]{0, 3, 6, 9}));
    }

    @Test public void testAdjacentBoxes() {
        // Dense boxes with equal-strength frogs that can interleave paths.
        assertEquals(3, FrogCrossing.maximumFrogs(
                new int[]{3, 3, 3}, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}));
    }

    @Test public void testSingleFrogCanLeapDirectly() {
        // Frog strong enough to leap entire canyon in one bound.
        assertEquals(1, FrogCrossing.maximumFrogs(
                new int[]{100}, new int[]{0, 5, 10, 15, 20}));
    }

    @Test public void testTwoBoxesOnlyEdges() {
        // Minimal canyon: just two edges, no intermediate boxes.
        assertEquals(1, FrogCrossing.maximumFrogs(
                new int[]{5}, new int[]{0, 5}));
        assertEquals(0, FrogCrossing.maximumFrogs(
                new int[]{4}, new int[]{0, 5}));
    }

    @Test public void testSharedResourceConflict() {
        // Second frog needs the same box that the first frog must use.
        // Boxes: 0, 4, 8. Gap is 4. Both frogs have strength 4.
        // First frog uses box 4 to reach 8. Second frog can't skip box 4.
        assertEquals(1, FrogCrossing.maximumFrogs(
                new int[]{4, 4}, new int[]{0, 4, 8}));
    }

    @Test public void testManyFrogsFewerBoxes() {
        // Five frogs but not enough intermediate boxes for all.
        assertEquals(4, FrogCrossing.maximumFrogs(
                new int[]{20, 17, 15, 13, 11},
                new int[]{7, 8, 11, 17, 20, 21, 24, 26, 31, 33, 38}));
    }

    @Test public void testStrongFrogsClearEasily() {
        // All frogs are massively overpowered; all cross easily.
        assertEquals(4, FrogCrossing.maximumFrogs(
                new int[]{50, 50, 50, 50}, new int[]{0, 5, 10, 15, 20}));
    }

    @Test public void testOnlyStrongestCrosses() {
        // Large gap that only the strongest frog can bridge.
        // Boxes: 0, 3, 10, 13. Gap from 3 to 10 is 7.
        assertEquals(1, FrogCrossing.maximumFrogs(
                new int[]{7, 5, 4}, new int[]{0, 3, 10, 13}));
    }

    @Test public void testBacktrackingRequired() {
        // The greedy choice for frog 1 blocks frog 2.
        // Frog 1 (str 5) could go 0->4->8, but that leaves no path for frog 2 (str 3).
        // Frog 1 should go 0->5->8, letting frog 2 go 0->3->6->8 (needs box 3,6).
        // Actually let's build this more carefully:
        // Boxes: 0, 3, 5, 6, 8. Frog1 str=5, Frog2 str=3.
        // Frog2 can go: 0->3->5->8(gap5>3 no), 0->3->6->8(gap2 ok), so needs 3 and 6.
        // Frog1 can go: 0->5->8(gap3 ok), using box 5 only.
        // Both cross! But if Frog1 greedily takes 0->3->8, box 3 gone, Frog2 stuck.
        assertEquals(2, FrogCrossing.maximumFrogs(
                new int[]{5, 3}, new int[]{0, 3, 5, 6, 8}));
    }

    // --- Mass / CRC tests ---

    @Test public void testMaximumFrogsTwenty() {
        massTest(20, 2316408012L);
    }

    @Test public void testMaximumFrogsHundred() {
        massTest(100, 595606895L);
    }

    private void massTest(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345 + n);
        int frogs = 3, count = 0, goal = 10;
        for(int i = 0; i < n; i++) {
            int[] strength = new int[frogs];
            for(int j = 0; j < frogs; j++) {
                strength[j] = 3 + rng.nextInt(4 * frogs);
            }
            Arrays.sort(strength);
            // Reverse to descending order.
            int j1 = 0, j2 = strength.length - 1;
            while(j1 < j2) {
                int tmp = strength[j1]; strength[j1] = strength[j2]; strength[j2] = tmp;
                j1++; j2--;
            }
            int m = frogs + rng.nextInt(2 * frogs);
            int[] boxes = new int[m];
            for(int j = 1; j < boxes.length; j++) {
                boxes[j] = boxes[j - 1] + 1 + rng.nextInt(1 + frogs);
            }
            int result = FrogCrossing.maximumFrogs(strength, boxes);
            check.update(result);
            if(++count == goal) {
                count = 0; goal++; frogs++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}