import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.zip.CRC32;
import java.util.Random;

public class PermutationsTestOne {

    @Test public void testInverseHundred() {
        testInverse(100, 2135030874L, false, 0);
    }
    
    @Test public void testInverseHundredThousand() {
        testInverse(100_000, 1999991L, false, 0);
    }
    
    @Test public void testSquareHundred() {
        testInverse(100, 2783838539L, false, 1);
    }
    
    @Test public void testSquareHundredThousand() {
        testInverse(100_000, 2415705031L, false, 1);
    }
    
    @Test public void testPowerHundred() {
        testInverse(100, 4179039232L, false, 2);
    }
    
    @Test public void testPowerTwoThousand() {
        testInverse(2000, 778167724L, false, 2);
    }
    
    @Test public void testPowerMillion() {
        testInverse(1_000_000, 3234036070L, false, 2);
    }
    
    private static void testInverse(int n, long expected, boolean verbose, int mode) {
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
            int[] res = null;
            if(mode == 0) { res = Permutations.inverse(perm); }
            if(mode == 1) { res = Permutations.square(perm); }
            if(mode == 2) { 
                res = Permutations.power(perm, i % 2 == 0 ? i : -i);
            }
            assertEquals(perm.length, res.length);
            // if(verbose || i == n - 1) {
                // System.out.println(i + ": " + Arrays.toString(perm) + " " + Arrays.toString(res));
            // }
            check.update(Arrays.toString(res).getBytes());
            if(mode == 0) {
                assertArrayEquals(perm, Permutations.inverse(res));
            }
        }
        assertEquals(expected, check.getValue());
    }
    
}
