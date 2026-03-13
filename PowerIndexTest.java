import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PowerIndexTest {

    // Turn this on to see the answers returned by your methods in mass tests.
    private static final boolean VERBOSE = false;

    // --- Helper: compute factorial for Shapley-Shubik sum check ---
    private static int factorial(int n) {
        int f = 1;
        for (int i = 2; i <= n; i++) { f *= i; }
        return f;
    }

    private static void assertShapleySum(int[] out) {
        int sum = 0;
        for (int v : out) { sum += v; }
        assertEquals("Shapley-Shubik values must sum to n!", factorial(out.length), sum);
    }

    // --- Banzhaf explicit tests ---

    @Test public void testBanzhaf() {
        int[] w1 = {4, 6, 10, 12};
        int[] r1 = new int[4];
        int[] e1 = {1, 3, 3, 5};
        PowerIndex.banzhaf(17, w1, r1);
        assertArrayEquals(e1, r1);

        int[] w2 = {20, 2, 5, 8, 19};
        int[] r2 = new int[5];
        int[] e2 = {9, 1, 1, 7, 7};
        PowerIndex.banzhaf(28, w2, r2);
        assertArrayEquals(e2, r2);

        int[] w3 = {3, 5, 16, 17, 26};
        int[] r3 = new int[5];
        int[] e3 = {2, 2, 6, 6, 10};
        PowerIndex.banzhaf(34, w3, r3);
        assertArrayEquals(e3, r3);
    }

    @Test public void testBanzhafDummyVoters() {
        // Spec example: {9,9,7,3,1,1} with quota 16.
        // Last three voters are dummies who can never be critical.
        int[] w = {9, 9, 7, 3, 1, 1};
        int[] out = new int[6];
        PowerIndex.banzhaf(16, w, out);
        assertArrayEquals(new int[]{16, 16, 16, 0, 0, 0}, out);
    }

    @Test public void testBanzhafDictatorship() {
        // Voter 3 has weight 31, others sum to 6. Quota 19.
        // Only voter 3 can ever be critical.
        int[] w = {1, 2, 3, 31};
        int[] out = new int[4];
        PowerIndex.banzhaf(19, w, out);
        assertArrayEquals(new int[]{0, 0, 0, 8}, out);
    }

    @Test public void testBanzhafEqualWeights() {
        // All voters symmetric: each should have equal Banzhaf index.
        int[] w = {5, 5, 5, 5};
        int[] out = new int[4];
        PowerIndex.banzhaf(11, w, out);
        assertArrayEquals(new int[]{3, 3, 3, 3}, out);

        int[] w5 = {1, 1, 1, 1, 1};
        int[] out5 = new int[5];
        PowerIndex.banzhaf(3, w5, out5);
        assertArrayEquals(new int[]{6, 6, 6, 6, 6}, out5);
    }

    @Test public void testBanzhafUnanimous() {
        // Quota equals total weight: only the grand coalition wins,
        // and every voter is critical in it.
        int[] w = {3, 5, 7};
        int[] out = new int[3];
        PowerIndex.banzhaf(15, w, out);
        assertArrayEquals(new int[]{1, 1, 1}, out);

        int[] w4 = {1, 1, 1, 1};
        int[] out4 = new int[4];
        PowerIndex.banzhaf(4, w4, out4);
        assertArrayEquals(new int[]{1, 1, 1, 1}, out4);
    }

    @Test public void testBanzhafTwoVoters() {
        // Two voters, only the big one can win alone.
        // Voter 0 (weight 3) is never critical since 7 >= 6 alone.
        int[] w = {3, 7};
        int[] out = new int[2];
        PowerIndex.banzhaf(6, w, out);
        assertArrayEquals(new int[]{0, 2}, out);

        // Two equal voters: both critical only in the grand coalition.
        int[] w2 = {5, 5};
        int[] out2 = new int[2];
        PowerIndex.banzhaf(6, w2, out2);
        assertArrayEquals(new int[]{1, 1}, out2);
    }

    @Test public void testBanzhafQuotaOne() {
        // Quota = 1: every single voter alone forms a winning coalition.
        // In any winning coalition, a voter is critical only if they're the sole member.
        // So each voter is critical exactly once (in their own singleton coalition).
        int[] w = {1, 2, 3};
        int[] out = new int[3];
        PowerIndex.banzhaf(1, w, out);
        assertArrayEquals(new int[]{1, 1, 1}, out);
    }

    @Test public void testBanzhafOneDummyAmongThree() {
        // weights {1, 10, 10} quota 11: voter 0 is not a dummy here!
        // {10,10} wins and voter 0 isn't needed, but {1,10} also wins
        // and voter with weight 10 is critical. Voter 0 is critical in {0,1} and {0,2}
        // since 1+10=11 >= 11 but 1 < 11.
        int[] w = {1, 10, 10};
        int[] out = new int[3];
        PowerIndex.banzhaf(11, w, out);
        assertArrayEquals(new int[]{2, 2, 2}, out);
    }

    // --- Shapley-Shubik explicit tests ---

    @Test public void testShapleyShubik() {
        int[] w1 = {1, 2, 2, 5};
        int[] r1 = new int[4];
        int[] e1 = {2, 2, 2, 18};
        PowerIndex.shapleyShubik(6, w1, r1);
        assertArrayEquals(e1, r1);
        assertShapleySum(r1);

        int[] w2 = {20, 19, 8, 5, 2};
        int[] r2 = new int[5];
        int[] e2 = {44, 34, 34, 4, 4};
        PowerIndex.shapleyShubik(28, w2, r2);
        assertArrayEquals(e2, r2);
        assertShapleySum(r2);

        int[] w3 = {1, 10, 15, 20, 27};
        int[] r3 = new int[5];
        int[] e3 = {0, 20, 20, 20, 60};
        PowerIndex.shapleyShubik(37, w3, r3);
        assertArrayEquals(e3, r3);
        assertShapleySum(r3);
    }

    @Test public void testShapleyShubikDummyVoters() {
        // Spec example: last three voters are dummies.
        int[] w = {9, 9, 7, 3, 1, 1};
        int[] out = new int[6];
        PowerIndex.shapleyShubik(16, w, out);
        assertArrayEquals(new int[]{240, 240, 240, 0, 0, 0}, out);
        assertShapleySum(out);
    }

    @Test public void testShapleyShubikDictatorship() {
        // Dictator gets all n! permutations.
        int[] w = {1, 2, 3, 31};
        int[] out = new int[4];
        PowerIndex.shapleyShubik(19, w, out);
        assertArrayEquals(new int[]{0, 0, 0, 24}, out);
        assertShapleySum(out);
    }

    @Test public void testShapleyShubikEqualWeights() {
        // Symmetric: each voter gets n!/n permutations.
        int[] w = {5, 5, 5, 5};
        int[] out = new int[4];
        PowerIndex.shapleyShubik(11, w, out);
        assertArrayEquals(new int[]{6, 6, 6, 6}, out);
        assertShapleySum(out);

        int[] w5 = {1, 1, 1, 1, 1};
        int[] out5 = new int[5];
        PowerIndex.shapleyShubik(3, w5, out5);
        assertArrayEquals(new int[]{24, 24, 24, 24, 24}, out5);
        assertShapleySum(out5);
    }

    @Test public void testShapleyShubikUnanimous() {
        // Unanimous: last voter in any permutation is always pivotal.
        // Each voter appears last in (n-1)! permutations, so each gets (n-1)!.
        // For n=3: each gets 2. For n=4: each gets 6.
        int[] w = {3, 5, 7};
        int[] out = new int[3];
        PowerIndex.shapleyShubik(15, w, out);
        assertArrayEquals(new int[]{2, 2, 2}, out);
        assertShapleySum(out);

        int[] w4 = {1, 1, 1, 1};
        int[] out4 = new int[4];
        PowerIndex.shapleyShubik(4, w4, out4);
        assertArrayEquals(new int[]{6, 6, 6, 6}, out4);
        assertShapleySum(out4);
    }

    @Test public void testShapleyShubikTwoVoters() {
        // {3,7} quota 6: voter 1 alone reaches quota, voter 0 never pivotal.
        int[] w = {3, 7};
        int[] out = new int[2];
        PowerIndex.shapleyShubik(6, w, out);
        assertArrayEquals(new int[]{0, 2}, out);
        assertShapleySum(out);

        // Two equal voters: both pivotal in exactly one permutation each.
        int[] w2 = {5, 5};
        int[] out2 = new int[2];
        PowerIndex.shapleyShubik(6, w2, out2);
        assertArrayEquals(new int[]{1, 1}, out2);
        assertShapleySum(out2);
    }

    @Test public void testShapleyShubikQuotaOne() {
        // Quota = 1: whoever comes first in the permutation is pivotal.
        // Each voter is first in (n-1)! permutations, so each gets (n-1)! = 2.
        int[] w = {1, 2, 3};
        int[] out = new int[3];
        PowerIndex.shapleyShubik(1, w, out);
        assertArrayEquals(new int[]{2, 2, 2}, out);
        assertShapleySum(out);
    }

    // --- Cross-method comparison ---

    @Test public void testMethodsAgreeOnDictatorship() {
        // Both methods assign all power to the dictator.
        int[] w = {1, 2, 3, 31};
        int[] bOut = new int[4];
        int[] sOut = new int[4];
        PowerIndex.banzhaf(19, w, bOut);
        PowerIndex.shapleyShubik(19, w, sOut);
        // Dictator (index 3) gets all power in both
        for (int i = 0; i < 3; i++) {
            assertEquals(0, bOut[i]);
            assertEquals(0, sOut[i]);
        }
        assertEquals(8, bOut[3]);
        assertEquals(24, sOut[3]);
    }

    @Test public void testMethodsAgreeOnSymmetry() {
        // Both methods give equal power to equal-weight voters.
        int[] w = {5, 5, 5, 5};
        int[] bOut = new int[4];
        int[] sOut = new int[4];
        PowerIndex.banzhaf(11, w, bOut);
        PowerIndex.shapleyShubik(11, w, sOut);
        for (int i = 1; i < 4; i++) {
            assertEquals(bOut[0], bOut[i]);
            assertEquals(sOut[0], sOut[i]);
        }
    }

    // --- CRC mass tests ---

    @Test public void massTestBanzhaf() {
        testPower(1755122046L, true, 350);
    }

    @Test public void massTestShapleyShubik() {
        testPower(2840684198L, false, 170);
    }

    private void testPower(long expected, boolean banzhaf, int limit) {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < limit; i++) {
            int nn = 4 + (i / 20);
            int[] weights = new int[nn];
            int[] out = new int[nn];
            int sum = 0;
            for(int j = 0; j < nn; j++) {
                weights[j] = 1 + rng.nextInt(5 + i);
                sum += weights[j];
            }
            Arrays.sort(weights);
            int quota = sum / 2 + 1;
            if(VERBOSE) {
                System.out.print(quota + ": " + Arrays.toString(weights) + (banzhaf ? " B" : " SS") + " -> ");
            }
            if(banzhaf) {
                PowerIndex.banzhaf(quota, weights, out);
            }
            else {
                PowerIndex.shapleyShubik(quota, weights, out);
            }
            if(VERBOSE) {
                System.out.println(Arrays.toString(out));
            }
            for(int e: out) { check.update(e); }
        }
        assertEquals(expected, check.getValue());
    }
}