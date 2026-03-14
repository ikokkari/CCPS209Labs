import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class MatchmakerTest {

    // --- Helper: verify matching is a valid permutation ---
    private static void assertValidMatching(int[] matching, int n) {
        assertEquals(n, matching.length);
        boolean[] seen = new boolean[n];
        for (int g : matching) {
            assertTrue("Girl " + g + " out of range", g >= 0 && g < n);
            assertTrue("Girl " + g + " paired twice", !seen[g]);
            seen[g] = true;
        }
    }

    // --- Helper: verify matching is stable ---
    private static void assertStable(int[] matching, int[][] boysPref, int[][] girlsPref) {
        int n = matching.length;
        int[] girlsMatch = new int[n];
        for (int b = 0; b < n; b++) { girlsMatch[matching[b]] = b; }

        int[][] boyRank = new int[n][n];
        int[][] girlRank = new int[n][n];
        for (int b = 0; b < n; b++) {
            for (int r = 0; r < n; r++) { boyRank[b][boysPref[b][r]] = r; }
        }
        for (int g = 0; g < n; g++) {
            for (int r = 0; r < n; r++) { girlRank[g][girlsPref[g][r]] = r; }
        }

        for (int b = 0; b < n; b++) {
            for (int g = 0; g < n; g++) {
                if (g != matching[b]) {
                    boolean bPrefersG = boyRank[b][g] < boyRank[b][matching[b]];
                    boolean gPrefersB = girlRank[g][b] < girlRank[g][girlsMatch[g]];
                    assertTrue("Unstable pair: boy " + b + " and girl " + g,
                            !(bPrefersG && gPrefersB));
                }
            }
        }
    }

    // --- Explicit tests ---

    @Test public void testSinglePair() {
        // n=1: only one possible matching
        int[][] boys = {{0}};
        int[][] girls = {{0}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{0}, result);
    }

    @Test public void testTwoPairsBoyOptimal() {
        // Spec's Chad/Norbert example: boys have compatible prefs, girls disagree.
        // Chad(0) prefers Stacy(0), Norbert(1) prefers Becky(1).
        // Stacy(0) prefers Norbert(1), Becky(1) prefers Chad(0).
        // Boy-optimal: each boy gets his first choice.
        int[][] boys = {{0, 1}, {1, 0}};
        int[][] girls = {{1, 0}, {0, 1}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{0, 1}, result);
        assertStable(result, boys, girls);
    }

    @Test public void testTwoPairsGirlOptimalSwapped() {
        // Running the same preferences with roles swapped gives the girl-optimal matching.
        int[][] boys = {{0, 1}, {1, 0}};
        int[][] girls = {{1, 0}, {0, 1}};
        int[] result = Matchmaker.galeShapley(girls, boys);
        // Girl-optimal: each "proposer" (now girls) gets first choice
        assertArrayEquals(new int[]{1, 0}, result);
        assertStable(result, girls, boys);
    }

    @Test public void testBothBoysPreferSameGirl() {
        // Both boys prefer girl 0. Both girls prefer boy 0.
        // Boy 0 gets girl 0 (his first choice). Boy 1 settles for girl 1.
        int[][] boys = {{0, 1}, {0, 1}};
        int[][] girls = {{0, 1}, {0, 1}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{0, 1}, result);
        assertStable(result, boys, girls);
    }

    @Test public void testNoConflicts() {
        // Each boy's first choice is unique: no one gets displaced.
        int[][] boys = {{0, 1, 2}, {1, 2, 0}, {2, 0, 1}};
        int[][] girls = {{0, 1, 2}, {1, 2, 0}, {2, 0, 1}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{0, 1, 2}, result);
        assertStable(result, boys, girls);
    }

    @Test public void testAllBoysSamePreference() {
        // Every boy wants girl 0 first. Girls' preferences break the competition.
        int[][] boys = {{0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
        int[][] girls = {{2, 0, 1}, {2, 0, 1}, {2, 0, 1}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        // Girl 0 picks boy 2 (her favorite). Boys 0,1 cascade down.
        assertArrayEquals(new int[]{1, 2, 0}, result);
        assertStable(result, boys, girls);
    }

    @Test public void testAllBoysPreferReverse() {
        // Every boy prefers girls in reverse order. Every girl prefers boys in order.
        int[][] boys = {{2, 1, 0}, {2, 1, 0}, {2, 1, 0}};
        int[][] girls = {{0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{2, 1, 0}, result);
        assertStable(result, boys, girls);
    }

    @Test public void testIdentityPreferences() {
        // Every boy prefers their numerical counterpart first.
        // Every girl prefers their numerical counterpart first.
        int[][] boys = {{0, 1, 2, 3}, {1, 3, 0, 2}, {2, 0, 3, 1}, {3, 0, 2, 1}};
        int[][] girls = {{0, 3, 2, 1}, {1, 3, 0, 2}, {2, 0, 3, 1}, {3, 1, 2, 0}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{0, 1, 2, 3}, result);
        assertStable(result, boys, girls);
        // Swapping roles should give same result in this symmetric case.
        assertArrayEquals(new int[]{0, 1, 2, 3}, Matchmaker.galeShapley(girls, boys));
    }

    @Test public void testCyclicPreferences() {
        // Boy i prefers girl (i+1)%5 first. Girl i prefers boy (i+1)%5 first.
        // Boy-optimal: each boy gets his first choice.
        int n = 5;
        int[][] boys = new int[n][n];
        int[][] girls = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boys[i][j] = (i + 1 + j) % n;
                girls[i][j] = (i + 1 + j) % n;
            }
        }
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{1, 2, 3, 4, 0}, result);
        assertStable(result, boys, girls);
    }

    @Test public void testDisplacementCascade() {
        // A scenario requiring multiple displacements.
        int[][] boys = {{1, 0, 2}, {0, 2, 1}, {2, 1, 0}};
        int[][] girls = {{2, 1, 0}, {0, 2, 1}, {1, 0, 2}};
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertValidMatching(result, 3);
        assertStable(result, boys, girls);
        assertArrayEquals(new int[]{1, 0, 2}, result);
    }

    @Test public void testFiveCandidates() {
        int[][] boys = {
                {1, 0, 3, 4, 2}, {2, 4, 1, 0, 3}, {3, 0, 4, 2, 1},
                {4, 0, 1, 2, 3}, {0, 1, 2, 3, 4}
        };
        int[][] girls = {
                {1, 2, 0, 4, 3}, {2, 4, 0, 1, 3}, {3, 4, 0, 1, 2},
                {4, 1, 0, 3, 2}, {0, 2, 3, 1, 4}
        };
        int[] result = Matchmaker.galeShapley(boys, girls);
        assertArrayEquals(new int[]{1, 2, 3, 4, 0}, result);
        assertStable(result, boys, girls);
        // Roles swapped gives same result in this case
        assertArrayEquals(new int[]{1, 2, 3, 4, 0}, Matchmaker.galeShapley(girls, boys));
    }

    @Test public void testStabilityOnRandomInstances() {
        // Generate several random instances and verify stability
        Random rng = new Random(42);
        for (int trial = 0; trial < 20; trial++) {
            int n = 3 + trial / 4;
            int[][] boys = new int[n][n];
            int[][] girls = new int[n][n];
            for (int j = 0; j < n; j++) {
                fillRandomPermutation(rng, boys[j]);
                fillRandomPermutation(rng, girls[j]);
            }
            int[] result = Matchmaker.galeShapley(boys, girls);
            assertValidMatching(result, n);
            assertStable(result, boys, girls);
        }
    }

    // --- Existing explicit tests ---

    @Test public void explicitTestCases() {
        int[][] boys0 = {{0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
        int[][] girls0 = {{2, 0, 1}, {2, 0, 1}, {2, 0, 1}};
        int[] exp0 = {1, 2, 0};
        assertArrayEquals(exp0, Matchmaker.galeShapley(boys0, girls0));

        int[][] boys1 = {{1, 2, 3, 0}, {2, 3, 0, 1}, {3, 0, 1, 2}, {0, 1, 2, 3}};
        int[][] girls1 = {{3, 1, 2, 0}, {1, 2, 0, 3}, {2, 0, 3, 1}, {0, 3, 1, 2}};
        int[] exp1 = {1, 2, 3, 0};
        assertArrayEquals(exp1, Matchmaker.galeShapley(boys1, girls1));

        int[][] boys2 = {{0, 1, 2, 3}, {1, 3, 0, 2}, {2, 0, 3, 1}, {3, 0, 2, 1}};
        int[][] girls2 = {{0, 3, 2, 1}, {1, 3, 0, 2}, {2, 0, 3, 1}, {3, 1, 2, 0}};
        int[] exp2 = {0, 1, 2, 3};
        assertArrayEquals(exp2, Matchmaker.galeShapley(boys2, girls2));
        assertArrayEquals(exp2, Matchmaker.galeShapley(girls2, boys2));

        int[][] boys3 = {{1, 0, 3, 4, 2}, {2, 4, 1, 0, 3}, {3, 0, 4, 2, 1}, {4, 0, 1, 2, 3}, {0, 1, 2, 3, 4}};
        int[][] girls3 = {{1, 2, 0, 4, 3}, {2, 4, 0, 1, 3}, {3, 4, 0, 1, 2}, {4, 1, 0, 3, 2}, {0, 2, 3, 1, 4}};
        int[] exp3 = {1, 2, 3, 4, 0};
        assertArrayEquals(exp3, Matchmaker.galeShapley(boys3, girls3));
        assertArrayEquals(exp3, Matchmaker.galeShapley(girls3, boys3));
    }

    // --- CRC mass tests ---

    private void fillRandomPermutation(Random rng, int[] a) {
        int n = a.length;
        for(int i = 0; i < n; i++) { a[i] = i; }
        for(int i = 1; i < n; i++) {
            int j = rng.nextInt(i + 1);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
    }

    @Test public void testGaleShapleyTen() {
        testGaleShapley(10, 3138738373L);
    }

    @Test public void testGaleShapleyHundred() {
        testGaleShapley(100, 76880458L);
    }

    @Test public void testGaleShapleyTwoThousand() {
        testGaleShapley(2000, 1327000569L);
    }

    private void testGaleShapley(int rounds, long expected) {
        Random rng = new Random(rounds);
        CRC32 check = new CRC32();
        int count = 1, goal = 1, n = 1;
        int[][] boysPref = new int[n][n], girlsPref = new int[n][n];
        for(int i = 0; i < rounds; i++) {
            for(int j = 0; j < n; j++) {
                fillRandomPermutation(rng, boysPref[j]);
                fillRandomPermutation(rng, girlsPref[j]);
            }
            int[] result = Matchmaker.galeShapley(boysPref, girlsPref);
            for(int e: result) { check.update(e); }
            if(count++ == goal) {
                count = 0;
                goal++;
                n++;
                boysPref = new int[n][n];
                girlsPref = new int[n][n];
            }
        }
        assertEquals(expected, check.getValue());
    }
}