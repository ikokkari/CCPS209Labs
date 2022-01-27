import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PowerIndexTest {

    // Turn this on to see the answers returned by your methods in mass tests.
    private static final boolean VERBOSE = false;
    
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
    
    @Test public void testShapleyShubik() {
        int[] w1 = {1, 2, 2, 5};
        int[] r1 = new int[4];
        int[] e1 = {2, 2, 2, 18};
        PowerIndex.shapleyShubik(6, w1, r1);
        assertArrayEquals(e1, r1);
        
        int[] w2 = {20, 19, 8, 5, 2};
        int[] r2 = new int[5];
        int[] e2 = {44, 34, 34, 4, 4};
        PowerIndex.shapleyShubik(28, w2, r2);
        assertArrayEquals(e2, r2);
        
        int[] w3 = {1, 10, 15, 20, 27};
        int[] r3 = new int[5];
        int[] e3 = {0, 20, 20, 20, 60};
        PowerIndex.shapleyShubik(37, w3, r3);
        assertArrayEquals(e3, r3);
    }
    
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