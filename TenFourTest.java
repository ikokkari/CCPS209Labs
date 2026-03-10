import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TenFourTest {

    // --- Helper to validate that each step in the path is a legal move ---

    private static void assertValidPath(List<Integer> path, int n, int limit) {
        assertTrue("Path should not be empty", path.size() > 0);
        assertEquals("Path must start at 4", 4, (int) path.get(0));
        assertEquals("Path must end at n", n, (int) path.get(path.size() - 1));
        for (int i = 0; i < path.size(); i++) {
            int v = path.get(i);
            assertTrue("All values in path must be < limit", v < limit);
            assertTrue("All values in path must be positive", v > 0);
        }
        for (int i = 0; i < path.size() - 1; i++) {
            int v = path.get(i), w = path.get(i + 1);
            boolean legal = (w == 10 * v) || (w == 10 * v + 4)
                    || (v % 2 == 0 && w == v / 2);
            assertTrue("Step from " + v + " to " + w + " is not a legal move", legal);
        }
    }

    // --- Trivial and base cases ---

    @Test public void testStartEqualsGoal() {
        // n == 4: already there, path is just [4]
        assertEquals("[4]", TenFour.shortestPath(4, 100).toString());
    }

    @Test public void testImmediateNeighboursOfFour() {
        // 4 * 10 = 40: one step
        assertEquals("[4, 40]", TenFour.shortestPath(40, 100).toString());

        // 4 * 10 + 4 = 44: one step
        assertEquals("[4, 44]", TenFour.shortestPath(44, 100).toString());

        // 4 / 2 = 2: one step
        assertEquals("[4, 2]", TenFour.shortestPath(2, 100).toString());
    }

    @Test public void testSmallTargets() {
        // 4 -> 2 -> 1 (two halvings)
        assertEquals("[4, 2, 1]", TenFour.shortestPath(1, 100).toString());

        // 4 -> 2 -> 24 -> 12 -> 6 -> 3
        assertEquals("[4, 2, 24, 12, 6, 3]", TenFour.shortestPath(3, 100).toString());

        // 4 -> 40 -> 20 -> 10 -> 5
        assertEquals("[4, 40, 20, 10, 5]", TenFour.shortestPath(5, 100).toString());

        // 4 -> 2 -> 24 -> 12 -> 6
        assertEquals("[4, 2, 24, 12, 6]", TenFour.shortestPath(6, 100).toString());

        // 4 -> 2 -> 1 -> 14 -> 7
        assertEquals("[4, 2, 1, 14, 7]", TenFour.shortestPath(7, 100).toString());

        // 4 -> 40 -> 20 -> 10
        assertEquals("[4, 40, 20, 10]", TenFour.shortestPath(10, 1000).toString());

        // 4 -> 40 -> 20
        assertEquals("[4, 40, 20]", TenFour.shortestPath(20, 100).toString());
    }

    // --- Original explicit tests ---

    @Test public void testShortestPathExplicit() {
        assertEquals("[4, 2, 24, 12, 6]", TenFour.shortestPath(6, 100).toString());
        assertEquals("[4, 2, 24, 12, 6, 3, 34, 17]", TenFour.shortestPath(17, 100).toString());
        assertEquals("[4, 2, 24, 12, 124, 62, 31]", TenFour.shortestPath(31, 1000).toString());
        assertEquals("[4, 2, 1, 14, 144, 72, 36]", TenFour.shortestPath(36, 1000).toString());
        assertEquals("[4, 40, 20, 10, 104, 1040, 520, 260, 130, 65]",
                TenFour.shortestPath(65, 10000).toString());
        assertEquals("[4, 2, 24, 12, 124, 62, 624, 312, 156, 78, 39, 394, 197]",
                TenFour.shortestPath(197, 1_000_000).toString());
    }

    // --- Uses all three move types ---

    @Test public void testAllMoveTypes() {
        // n=100: 4 ->(*10) 40 ->(*10) 400 ->(/2) 200 ->(/2) 100
        // Uses *10, *10, /2, /2 — demonstrates all move types
        assertEquals("[4, 40, 400, 200, 100]", TenFour.shortestPath(100, 10000).toString());

        // n=500: path goes through *10 and /2
        assertEquals("[4, 40, 400, 4000, 2000, 1000, 500]",
                TenFour.shortestPath(500, 1_000_000).toString());

        // n=42: longer path involving the *10+4 move
        assertEquals("[4, 2, 24, 12, 6, 64, 32, 16, 8, 84, 42]",
                TenFour.shortestPath(42, 10000).toString());
    }

    // --- Limit boundary tests ---

    @Test public void testLimitExactlyAtGoal() {
        // limit == n: goal is not reachable (must be strictly less than limit)
        assertEquals("[]", TenFour.shortestPath(40, 40).toString());
    }

    @Test public void testLimitOneAboveGoal() {
        // limit = n + 1: goal itself fits, but intermediates might not
        // n=40, limit=41: path is [4, 40], 40 < 41 works
        assertEquals("[4, 40]", TenFour.shortestPath(40, 41).toString());
    }

    @Test public void testLimitTooSmallForIntermediates() {
        // n=5 needs path 4->40->20->10->5, max intermediate is 40
        // limit=10: can't reach 40, so unreachable
        assertEquals("[]", TenFour.shortestPath(5, 10).toString());

        // limit=41: 40 < 41, so this works
        assertEquals("[4, 40, 20, 10, 5]", TenFour.shortestPath(5, 41).toString());

        // n=65 with limit=10000 takes shortest path through 1040
        assertEquals("[4, 40, 20, 10, 104, 1040, 520, 260, 130, 65]",
                TenFour.shortestPath(65, 10000).toString());
        // n=65 with limit=1000: can't use 1040, finds a longer path via 52 instead
        assertEquals("[4, 40, 20, 10, 104, 52, 520, 260, 130, 65]",
                TenFour.shortestPath(65, 1000).toString());
    }

    @Test public void testUnreachableSmallLimit() {
        // n=6 with limit=5: can't even use starting point's immediate neighbors usefully
        assertEquals("[]", TenFour.shortestPath(6, 5).toString());

        // n=1 needs 4->2->1; limit must be > 4 (for start) and > 2 (intermediate)
        // limit=3 means 2 < 3 works, but 4 is the start...
        // Actually seen[4] is set, and 4 < limit=5 is needed for array bounds
        // With limit=3, array size 3 means index 4 is out of bounds. So limit must be > 4.
        assertEquals("[4, 2, 1]", TenFour.shortestPath(1, 5).toString());
    }

    // --- Path validity for various targets ---

    @Test public void testPathValidity() {
        // Verify structural properties of paths for a range of targets
        int[] targets = {1, 2, 3, 5, 6, 7, 8, 9, 10, 15, 20, 25, 31, 36, 42, 50, 65, 99, 100};
        for (int n : targets) {
            int limit = 100;
            List<Integer> result;
            do {
                result = TenFour.shortestPath(n, limit);
                limit *= 2;
            } while (result.isEmpty());
            assertValidPath(result, n, limit);
        }
    }

    // --- Longer paths ---

    @Test public void testLongerPaths() {
        // n=99 requires a fairly long path
        List<Integer> path99 = TenFour.shortestPath(99, 100000);
        assertEquals("[4, 40, 400, 200, 100, 50, 504, 252, 126, 1264, 632, 316, 158, 1584, 792, 396, 198, 99]",
                path99.toString());

        // n=197 (already tested, but verify path length = 13)
        List<Integer> path197 = TenFour.shortestPath(197, 1_000_000);
        assertEquals(13, path197.size());
    }

    // --- CRC mass test ---

    @Test public void testShortestPathMass() {
        int maxlimit = 50_000;
        CRC32 check = new CRC32();
        for(int i = 1; i < 500; i++) {
            int limit = 50_000 + 1;
            List<Integer> result;
            do {
                result = TenFour.shortestPath(i, limit);
                limit = limit * 2;
                maxlimit = Math.max(maxlimit, limit);
            } while(result.size() == 0);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(1154212991, check.getValue());
    }
}