import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PermutationsTestTwo {

    private static String alpha;
    static { // 64 symbols for permutations elements should be quite enough
        alpha = "abcdefghijklmnopqrstuvwxyz";
        alpha = alpha + alpha.toUpperCase();
        alpha = "0123456789" + alpha;
    }

    // --- toCycles explicit tests ---

    @Test public void testToCyclesIdentity() {
        // Identity: every element is a fixed point, all singleton cycles.
        int[] id = {0, 1, 2, 3};
        List<List<Integer>> c = Permutations.toCycles(id);
        assertEquals("[[0], [1], [2], [3]]", c.toString());
    }

    @Test public void testToCyclesSingleTransposition() {
        // Swap 0 and 1, rest fixed. One 2-cycle starting from max (1), then singletons.
        int[] swap = {1, 0, 2, 3};
        List<List<Integer>> c = Permutations.toCycles(swap);
        assertEquals("[[1, 0], [2], [3]]", c.toString());
    }

    @Test public void testToCyclesThreeCycle() {
        // Single 3-cycle: 0->1->2->0. Canonical form starts from max element (2).
        int[] p = {1, 2, 0};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[2, 0, 1]]", c.toString());
    }

    @Test public void testToCyclesFourCycle() {
        // Single 4-cycle: 0->1->2->3->0. Starts from max (3).
        int[] p = {1, 2, 3, 0};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[3, 0, 1, 2]]", c.toString());
    }

    @Test public void testToCyclesDisjointTranspositions() {
        // Two disjoint swaps: (0,1) and (2,3).
        int[] p = {1, 0, 3, 2};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[1, 0], [3, 2]]", c.toString());
    }

    @Test public void testToCyclesMixed() {
        // One 3-cycle (0,1,2), one 2-cycle (3,4), one fixed point (5).
        int[] p = {1, 2, 0, 4, 3, 5};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[2, 0, 1], [4, 3], [5]]", c.toString());
    }

    @Test public void testToCyclesSpecPermutation() {
        // {2,1,4,0,5,3}: element 1 is a fixed point, rest form a 5-cycle.
        int[] p = {2, 1, 4, 0, 5, 3};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[1], [5, 3, 0, 2, 4]]", c.toString());
    }

    @Test public void testToCyclesReverse() {
        // Reverse of 6 elements: three disjoint transpositions.
        int[] p = {5, 4, 3, 2, 1, 0};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[3, 2], [4, 1], [5, 0]]", c.toString());
    }

    @Test public void testToCyclesAllTranspositions() {
        // 8 elements, all disjoint 2-cycles.
        int[] p = {7, 3, 5, 1, 6, 2, 4, 0};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[3, 1], [5, 2], [6, 4], [7, 0]]", c.toString());
    }

    @Test public void testToCyclesSizeOne() {
        int[] p = {0};
        List<List<Integer>> c = Permutations.toCycles(p);
        assertEquals("[[0]]", c.toString());
    }

    @Test public void testToCyclesSizeTwo() {
        // Identity of size 2
        assertEquals("[[0], [1]]", Permutations.toCycles(new int[]{0, 1}).toString());
        // Swap of size 2
        assertEquals("[[1, 0]]", Permutations.toCycles(new int[]{1, 0}).toString());
    }

    @Test public void testToCyclesCanonicalForm() {
        // Verify canonical properties: each cycle starts from its max,
        // cycles sorted ascending by first element.
        int[][] perms = {
                {1, 0, 2, 3}, {1, 2, 0}, {1, 2, 3, 0},
                {1, 0, 3, 2}, {2, 1, 4, 0, 5, 3}, {5, 4, 3, 2, 1, 0},
                {7, 3, 5, 1, 6, 2, 4, 0}, {1, 2, 0, 4, 3, 5}
        };
        for (int[] perm : perms) {
            List<List<Integer>> cycles = Permutations.toCycles(perm);
            // Each cycle starts from its largest element
            for (List<Integer> cycle : cycles) {
                int max = cycle.stream().mapToInt(Integer::intValue).max().getAsInt();
                assertEquals("Cycle should start from its max element",
                        max, (int) cycle.get(0));
            }
            // Cycles sorted ascending by first element
            for (int i = 1; i < cycles.size(); i++) {
                assertTrue("Cycles should be sorted by first element",
                        cycles.get(i).get(0) > cycles.get(i - 1).get(0));
            }
        }
    }

    // --- fromCycles explicit tests ---

    @Test public void testFromCyclesRoundTrip() {
        // toCycles then fromCycles should give back the original permutation.
        int[][] perms = {
                {0}, {0, 1}, {1, 0}, {1, 2, 0}, {1, 2, 3, 0},
                {0, 1, 2, 3}, {1, 0, 3, 2}, {2, 1, 4, 0, 5, 3},
                {1, 2, 0, 4, 3, 5}, {5, 4, 3, 2, 1, 0},
                {7, 3, 5, 1, 6, 2, 4, 0}
        };
        for (int[] perm : perms) {
            List<List<Integer>> cycles = Permutations.toCycles(perm);
            int[] back = Permutations.fromCycles(cycles);
            assertArrayEquals("Round-trip failed for " + Arrays.toString(perm), perm, back);
        }
    }

    @Test public void testFromCyclesIdentity() {
        List<List<Integer>> cycles = Permutations.toCycles(new int[]{0, 1, 2, 3});
        assertArrayEquals(new int[]{0, 1, 2, 3}, Permutations.fromCycles(cycles));
    }

    @Test public void testFromCyclesSingleCycle() {
        // Single 3-cycle [[2, 0, 1]] -> {1, 2, 0}
        List<List<Integer>> cycles = Permutations.toCycles(new int[]{1, 2, 0});
        assertArrayEquals(new int[]{1, 2, 0}, Permutations.fromCycles(cycles));
    }

    // --- cycles string explicit tests ---

    @Test public void testCyclesStringIdentity() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{0, 1, 2, 3});
        assertEquals("(0)(1)(2)(3)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringTransposition() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 0, 2, 3});
        assertEquals("(10)(2)(3)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringThreeCycle() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 2, 0});
        assertEquals("(201)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringFourCycle() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 2, 3, 0});
        assertEquals("(3012)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringMixed() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 2, 0, 4, 3, 5});
        assertEquals("(201)(43)(5)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringSpecPerm() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{2, 1, 4, 0, 5, 3});
        assertEquals("(1)(53024)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringReverse() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{5, 4, 3, 2, 1, 0});
        assertEquals("(32)(41)(50)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringAlphabetMapping() {
        // Elements >= 10 use letters: 10='a', 11='b', etc.
        // 12-element permutation: single 12-cycle.
        int[] p = new int[12];
        for (int i = 0; i < 12; i++) { p[i] = (i + 1) % 12; }
        // Cycle: 11->0->1->...->10->11, canonical starts from max (11)
        List<List<Integer>> c = Permutations.toCycles(p);
        // 11='b', 0='0', 1='1', ..., 9='9', 10='a'
        assertEquals("(b0123456789a)", Permutations.cycles(c, alpha));
    }

    @Test public void testCyclesStringSizeOne() {
        List<List<Integer>> c = Permutations.toCycles(new int[]{0});
        assertEquals("(0)", Permutations.cycles(c, alpha));
    }

    // --- parity explicit tests ---

    @Test public void testParityIdentity() {
        // All singleton cycles (odd length). Zero even-length cycles => parity +1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{0, 1, 2, 3});
        assertEquals(1, Permutations.parity(c));
    }

    @Test public void testParitySingleTransposition() {
        // One 2-cycle (even length). One even-length cycle => odd count => -1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 0, 2, 3});
        assertEquals(-1, Permutations.parity(c));
    }

    @Test public void testParityThreeCycle() {
        // One 3-cycle (odd length). Zero even-length cycles => +1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 2, 0});
        assertEquals(1, Permutations.parity(c));
    }

    @Test public void testParityFourCycle() {
        // One 4-cycle (even length) => -1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 2, 3, 0});
        assertEquals(-1, Permutations.parity(c));
    }

    @Test public void testParityDisjointTranspositions() {
        // Two 2-cycles: even count of even-length cycles => +1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 0, 3, 2});
        assertEquals(1, Permutations.parity(c));
    }

    @Test public void testParityMixed() {
        // {1,2,0,4,3,5}: one 3-cycle (odd), one 2-cycle (even), one singleton (odd).
        // One even-length cycle => odd count => -1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{1, 2, 0, 4, 3, 5});
        assertEquals(-1, Permutations.parity(c));
    }

    @Test public void testParityReverse() {
        // Reverse of 6: three 2-cycles. Three even-length cycles => odd count => -1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{5, 4, 3, 2, 1, 0});
        assertEquals(-1, Permutations.parity(c));

        // Reverse of 5: two 2-cycles + one fixed point. Two even-length => even count => +1.
        List<List<Integer>> c5 = Permutations.toCycles(new int[]{4, 3, 2, 1, 0});
        assertEquals(1, Permutations.parity(c5));
    }

    @Test public void testParityFourTranspositions() {
        // {7,3,5,1,6,2,4,0}: four 2-cycles. Four even-length => even count => +1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{7, 3, 5, 1, 6, 2, 4, 0});
        assertEquals(1, Permutations.parity(c));
    }

    @Test public void testParitySizeOne() {
        // Single fixed point. Zero even-length cycles => +1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{0});
        assertEquals(1, Permutations.parity(c));
    }

    @Test public void testParityFiveCycle() {
        // 5-cycle (odd length) => +1.
        List<List<Integer>> c = Permutations.toCycles(new int[]{2, 1, 4, 0, 5, 3});
        // This is [1] + [5,3,0,2,4] = singleton(odd) + 5-cycle(odd) => +1
        assertEquals(1, Permutations.parity(c));
    }

    // --- CRC mass tests ---

    @Test public void testCyclesHundred() {
        testCycles(100, 2576563183L);
    }

    @Test public void testCyclesTenThousand() {
        testCycles(10_000, 3190148641L);
    }

    // Greatest common divisor
    private static int gcd(int a, int b) {
        while(b > 0) {
            int r = a % b; a = b; b = r;
        }
        return a;
    }

    // Least common multiple
    private static int lcm(int a, int b) {
        int g = gcd(a, b);
        return a * (b / g);
    }

    private void testCycles(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int goal = 2, nn = 2;
        for(int i = 0; i < n; i++) {
            if(i == goal) { nn++; goal = 2 * goal; }
            int[] perm = new int[nn];
            for(int j = 0; j < nn; j++) { perm[j] = j; }
            for(int k = 0; k < nn; k++) {
                int s = rng.nextInt(nn - k) + k;
                int tmp = perm[k]; perm[k] = perm[s]; perm[s] = tmp;
            }
            // Ask the student code to compute the cycles and the parity of the permutation.
            List<List<Integer>> cycles = Permutations.toCycles(perm);
            int parity = Permutations.parity(cycles);
            check.update(parity);
            // The order of the partition is the lcm of its cycle lengths.
            int order = 1;
            for(List<Integer> cycle: cycles) { order = lcm(order, cycle.size()); }
            // The power order-1 is not quite enough to reach identity...
            int[] almost = Permutations.power(perm, order - 1);
            // But the next one surely is.
            int[] oneMore = Permutations.chain(perm, almost);
            boolean identity = true;
            for(int j = 0; j < nn; j++) {
                assertEquals(j, oneMore[j]);
                identity &= (almost[j] == j);
            }
            assertTrue(order == 1 || !identity);
            String pretty = Permutations.cycles(cycles, alpha);
            try {
                check.update(pretty.getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            int[] back = Permutations.fromCycles(cycles);
            assertArrayEquals(perm, back);
        }
        assertEquals(expected, check.getValue());
    }
}