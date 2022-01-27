import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultipleWinnerElectionTest {

    @Test public void testDHondt() {
        int[] v1 = {6, 26, 33};
        int[] r1 = {1, 5, 6};
        assertArrayEquals(r1, MultipleWinnerElection.DHondt(v1, 12));
        int[] v2 = {44, 46, 86};
        int[] r2 = {2, 3, 5};
        assertArrayEquals(r2, MultipleWinnerElection.DHondt(v2, 10));
        int[] v3 = {130, 19, 31, 77};
        int[] r3 = {12, 1, 3, 7};
        assertArrayEquals(r3, MultipleWinnerElection.DHondt(v3, 23));
    }
    
    @Test public void testWebster() {
        int[] v1 = {6, 26, 33};
        int[] r1 = {1, 5, 6};
        assertArrayEquals(r1, MultipleWinnerElection.webster(v1, 12));
        int[] v2 = {44, 46, 86};
        int[] r2 = {2, 3, 5};
        assertArrayEquals(r2, MultipleWinnerElection.webster(v2, 10));
        int[] v3 = {130, 19, 31, 77};
        int[] r3 = {11, 2, 3, 7};
        assertArrayEquals(r3, MultipleWinnerElection.webster(v3, 23));
    }
    
    @Test public void testImperiali() {
        int[] v1 = {6, 26, 33};
        int[] r1 = {0, 5, 7};
        assertArrayEquals(r1, MultipleWinnerElection.imperiali(v1, 12));
        int[] v2 = {44, 46, 86};
        int[] r2 = {2, 2, 6};
        assertArrayEquals(r2, MultipleWinnerElection.imperiali(v2, 10));
        int[] v3 = {130, 19, 31, 77};
        int[] r3 = {13, 1, 2, 7};
        assertArrayEquals(r3, MultipleWinnerElection.imperiali(v3, 23));
    }
    
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
            int[] result0 = null, result1 = null, result2 = null;
            if(mode == 0 || mode == 3) { // DHondt
                result0 = MultipleWinnerElection.DHondt(votes, seats);
                assertTrue(isSorted(result0));
                try {
                    check.update(Arrays.toString(result0).getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) { }
            }
            if(mode == 1 || mode == 3) { // Webster
                result1 = MultipleWinnerElection.webster(votes, seats);
                assertTrue(isSorted(result1));
                try {
                    check.update(Arrays.toString(result1).getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) { }
            }
            if(mode == 2 || mode == 3) { // Imperiali
                result2 = MultipleWinnerElection.imperiali(votes, seats);
                assertTrue(isSorted(result2));
                try {
                    check.update(Arrays.toString(result2).getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) { }
            }
        }
        assertEquals(expected, check.getValue());
    }   
}