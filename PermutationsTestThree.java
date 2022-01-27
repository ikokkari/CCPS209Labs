import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PermutationsTestThree {

    @Test public void testFactoradicHundred() {
        testFactoradic(100, 6, 1614347970L);
    }
    
    @Test public void testFactoradicTenThousand() {
        testFactoradic(10_000, 11, 1386970017L);
    }
    
    @Test public void testFactoradicMillion() {
        testFactoradic(1_000_000, 16, 4173735546L);
    }
    
    private void testFactoradic(int n, int m, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        long curr = 1;
        int step = 2, goal = 10;
        int[] coeff = new int[m];
        for(int i = 0; i < n; i++) {
            Arrays.fill(coeff, 0);
            Permutations.toFactoradic(curr, coeff);
            try {
                check.update(Arrays.toString(coeff).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            long back = Permutations.fromFactoradic(coeff);
            assertEquals(curr, back);
            curr += 1 + rng.nextInt(step);
            if(i == goal) { goal = 2 * goal; step = 3 * step; }
            
        }
        assertEquals(expected, check.getValue());
    }
    
    @Test public void testKeyHundred() {
        testKey(100);
    }
    
    @Test public void testKeyHundredThousand() {
        testKey(100_000);
    }
    
    private void testKey(long n) {
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
            //System.out.println("perm is " + Arrays.toString(perm));
            long key = Permutations.toKey(perm);
            int[] back = Permutations.fromKey(key, nn);
            //System.out.println("back is " + Arrays.toString(back) + "\n");
            assertArrayEquals(perm, back);
        }
    }
    
    @Test public void testLehmerHundred() {
        testLehmer(100, 631991195L);
    }
    
    @Test public void testLehmerThousand() {
        testLehmer(1000, 334055023L);
    }
    
    @Test public void testLehmerTenThousand() {
        testLehmer(10_000, 4233414032L);
    }
    
    private void testLehmer(int n, long expected) {
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
            int[] inv = Permutations.toLehmer(perm);
            try {
                check.update(Arrays.toString(inv).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            int[] back = Permutations.fromLehmer(inv);
            assertArrayEquals(perm, back);
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testKeyConversion() {
        long f = 1;
        for(int i = 2; i <= 9; i++) { f = f * i; }
        int[] perm, prev = null;
        for(long key = 0; key < f; key++) {
            perm = Permutations.fromKey(key, 9);
            // Each permutation must be lexicographically greater than the previous one.
            for(int p = 0; key > 0 && p <= 9; p++) {
                assertTrue(p < 9); // This loop must terminate before reaching p == n.
                if(perm[p] > prev[p]) { break; }
                assertEquals(perm[p], prev[p]);
            }
            prev = perm;
        }
    }
    
}