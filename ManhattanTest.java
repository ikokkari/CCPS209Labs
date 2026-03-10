import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class ManhattanTest {

    // --- Explicit tests ---

    @Test public void testSingleBuilding() {
        assertEquals(12, Manhattan.totalArea(
                new int[]{1}, new int[]{5}, new int[]{3}));
    }

    @Test public void testTwoDisjoint() {
        // Two buildings with no overlap.
        assertEquals(32, Manhattan.totalArea(
                new int[]{1, 10}, new int[]{5, 15}, new int[]{3, 4}));
    }

    @Test public void testTwoAdjacentBuildings() {
        // One ends exactly where the next starts.
        assertEquals(47, Manhattan.totalArea(
                new int[]{1, 5}, new int[]{5, 10}, new int[]{3, 7}));
    }

    @Test public void testOverlappingSameHeight() {
        // Two overlapping buildings of equal height; overlap counted once.
        assertEquals(28, Manhattan.totalArea(
                new int[]{1, 3}, new int[]{6, 8}, new int[]{4, 4}));
    }

    @Test public void testBuildingContainedInAnother() {
        // Shorter wider building contains a taller narrower one.
        // x=1..3: h=5, x=3..7: h=8, x=7..10: h=5 -> 10+32+15 = 57
        assertEquals(57, Manhattan.totalArea(
                new int[]{1, 3}, new int[]{10, 7}, new int[]{5, 8}));
    }

    @Test public void testBuildingCompletelyHidden() {
        // Smaller building entirely behind a taller one.
        assertEquals(90, Manhattan.totalArea(
                new int[]{1, 2}, new int[]{10, 8}, new int[]{10, 5}));
    }

    @Test public void testTallNarrowAndShortWide() {
        // Tall narrow building on top of short wide one.
        // x=1..3: h=100, x=3..10: h=5 -> 200+35 = 235
        assertEquals(235, Manhattan.totalArea(
                new int[]{1, 1}, new int[]{3, 10}, new int[]{100, 5}));
    }

    @Test public void testThreeWithMiddleTallest() {
        // x=1..3: h=2, x=3..8: h=5, x=8..10: h=3 -> 4+25+6 = 35
        assertEquals(35, Manhattan.totalArea(
                new int[]{1, 3, 7}, new int[]{6, 8, 10}, new int[]{2, 5, 3}));
    }

    @Test public void testIdenticalStartDifferentEnd() {
        // Two buildings starting at same point, different widths.
        // x=1..5: h=3, x=5..6: h=3 -> 15
        assertEquals(15, Manhattan.totalArea(
                new int[]{1, 1}, new int[]{5, 6}, new int[]{3, 3}));
    }

    @Test public void testOriginalExplicit() {
        int[] s1 = {2, 6, 9, 12, 15};
        int[] e1 = {3, 8, 10, 14, 20};
        int[] h1 = {3, 3, 4, 3, 2};
        assertEquals(29, Manhattan.totalArea(s1, e1, h1));

        int[] s2 = {3, 7, 8, 17, 24, 26, 27};
        int[] e2 = {5, 18, 18, 28, 37, 29, 41};
        int[] h2 = {1, 3, 2, 2, 3, 2, 1};
        assertEquals(90, Manhattan.totalArea(s2, e2, h2));
    }

    // --- Mass / CRC tests ---

    @Test public void testMassTest() {
        CRC32 check = new CRC32();
        Random rng = new Random(777);
        for(int i = 2; i < 500; i++) {
            int n = rng.nextInt(3 * i) + 1;
            int[] s = new int[n];
            int[] e = new int[n];
            int[] h = new int[n];
            for(int j = 0; j < n; j++) {
                s[j] = rng.nextInt(4 * n);
            }
            Arrays.sort(s);
            for(int j = 0; j < n; j++) {
                int w = 1 + rng.nextInt(2 * n);
                e[j] = s[j] + w;
                if(j > 0 && s[j - 1] == s[j]) {
                    e[j] = Math.max(e[j], e[j - 1] + 1);
                }
                h[j] = 1 + (j / w) + rng.nextInt(3);
            }
            int r = Manhattan.totalArea(s, e, h);
            check.update(r);
        }
        assertEquals(2174298203L, check.getValue());
    }
}