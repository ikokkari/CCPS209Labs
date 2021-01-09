import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.zip.CRC32;

public class PowerIndexTest {

    private static final boolean VERBOSE = false;
    
    @Test public void testBanzhaf() {
        testPower(100, 1287582719L, true);
    }
    
    @Test public void testShapleyShubik() {
        testPower(100, 1214504174L, false);
    }
    
    private void testPower(int n, long expected, boolean banzhaf) {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < n; i++) {
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
            check.update(Arrays.toString(out).getBytes());
        }
        assertEquals(expected, check.getValue());
    }
    
}
