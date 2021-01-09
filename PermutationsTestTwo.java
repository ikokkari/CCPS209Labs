import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.zip.CRC32;
import java.util.Random;

public class PermutationsTestTwo {

    private static String alpha;
    static { // 64 symbols for permutations elements should be quite enough
        alpha = "abcdefghijklmnopqrstuvwxyz";
        alpha = alpha + alpha.toUpperCase();
        alpha = "0123456789" + alpha;
    }
    
    @Test public void testCyclesHundred() {
        testCycles(100, 2576563183L, false);
    }
    
    @Test public void testCyclesTenThousand() {
        testCycles(10_000, 3190148641L, false);
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
    
    private void testCycles(int n, long expected, boolean verbose) {
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
            check.update(pretty.getBytes());
            if(verbose) {
                //System.out.println(Arrays.toString(perm) + " " + cycles);
                System.out.println(Arrays.toString(perm) + "( " + (parity == -1 ? "-1" : "+1") + ") -> " + pretty);
            }
            int[] back = Permutations.fromCycles(cycles);
            assertArrayEquals(perm, back);
        }
        assertEquals(expected, check.getValue());
    }
}
