import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.zip.CRC32;
import java.util.Random;

public class P2J10Test {
    
    private static void fill(boolean[] v, Random rng) {
        for(int i = 0; i < v.length; i++) {
            v[i] = rng.nextBoolean();
        }
        // Ensure that at least one element is true no matter what.
        v[rng.nextInt(v.length)] = true;
    }
    
    private static final int N = 1000;
    
    @Test public void testJaccard() { testDissimilarity(N, 619021331L, 0); }
    @Test public void testMatching() { testDissimilarity(N, 2582992579L, 1); }
    @Test public void testDice() { testDissimilarity(N, 864445653L, 2); }
    @Test public void testRogersTanimono() { testDissimilarity(N, 2631246168L, 3); }
    @Test public void testRussellRao() { testDissimilarity(N, 3219060315L, 4); }
    @Test public void testSokalSneath() { testDissimilarity(N, 737788739L, 5); }
    
    private void testDissimilarity(int n, long expected, int mode) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int nn = 5 + i / 20;
            boolean[] v1 = new boolean[nn], v2 = new boolean[nn];
            fill(v1, rng); fill(v2, rng);
            Fraction result;
            if(mode == 0) { result = P2J10.jaccardDissimilarity(v1, v2); }
            else if(mode == 1) { result = P2J10.matchingDissimilarity(v1, v2); }
            else if(mode == 2) { result = P2J10.diceDissimilarity(v1, v2); }
            else if(mode == 3) { result = P2J10.rogersTanimonoDissimilarity(v1, v2); }
            else if(mode == 4) { result = P2J10.russellRaoDissimilarity(v1, v2); }
            else { result = P2J10.sokalSneathDissimilarity(v1, v2); }
            check.update(result.getNum().intValue());
            check.update(result.getDen().intValue());
            // if(i < 10) {
                // System.out.println(mode + " " + 
                // Arrays.toString(v1) + " " + Arrays.toString(v2) + " " + result);
            // }
        }
        assertEquals(expected, check.getValue());
    }
    
}
