import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.zip.CRC32;

public class MultipleWinnerElectionTest {

    @Test public void testDHondtHundred() {
        test(100, 3816530178L, 0);
    }
    
    @Test public void testDHondtTenThousand() {
        test(10000, 572303332L, 0);
    }
    
    @Test public void testWebsterHundred() {
        test(100, 2959731999L, 1);
    }
    
    @Test public void testWebsterTenThousand() {
        test(10000, 2360920111L, 1);
    }
    
    @Test public void testImperialiHundred() {
        test(100, 2278224169L, 2);
    }
    
    @Test public void testImperialiTenThousand() {
        test(10000, 2082193870L, 2);
    }
    
    @Test public void testAll() {
        test(1000, 156607936L, 3);
    }
    
    private boolean isSorted(int[] a) {
        int prev = a[0] - 1;
        for(int e : a) {
            if(e < prev) { return false; }
            prev = e;
        }
        return true;
    }
    
    private void test(int n, long expected, int mode) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int diffCount = 0;
        for(int i = 0; i < n; i++) {
            int C = 2 + Math.min(i / 20, 20) + Math.min(i / 1000, 20);
            int[] votes = new int[C];
            int seats = 10 + rng.nextInt(4 * C);
            for(int j = 0; j < C; j++) {
                votes[j] += rng.nextInt(i + 10);
                int c = rng.nextInt(C);
                votes[c] += rng.nextInt(2 * i + 1);
            }
            Arrays.sort(votes);
            //System.out.print(Arrays.toString(votes) + " for " + seats + " seats: ");
            int[] result0 = null, result1 = null, result2 = null;
            if(mode == 0 || mode == 3) { // DHondt
                result0 = MultipleWinnerElection.DHondt(votes, seats);
                assertTrue(isSorted(result0));
                check.update(Arrays.toString(result0).getBytes());
            }
            if(mode == 1 || mode == 3) { // Webster
                result1 = MultipleWinnerElection.webster(votes, seats);
                assertTrue(isSorted(result1));
                check.update(Arrays.toString(result1).getBytes());
            }
            if(mode == 2 || mode == 3) { // Imperiali
                result2 = MultipleWinnerElection.imperiali(votes, seats);
                assertTrue(isSorted(result2));
                check.update(Arrays.toString(result2).getBytes());
            }
            if(mode == 3) { // For examining how the methods produce different results.
                if(!(Arrays.equals(result0, result1) && Arrays.equals(result1, result2))) {
                    diffCount++;
                    // System.out.print(Arrays.toString(votes) + " " + seats + ": ");
                    // System.out.print(Arrays.toString(result0));
                    // System.out.print(Arrays.toString(result1));
                    // System.out.println(Arrays.toString(result2));
                }
            }
        }
        if(mode == 3) {
            assertEquals(981, diffCount);
        }
        assertEquals(expected, check.getValue());
    }
    
}
