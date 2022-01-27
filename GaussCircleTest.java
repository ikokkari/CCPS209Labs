import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class GaussCircleTest {

    private static final int[][] expected = {
        // r, inside, border, edge
        {3, 13, 12, 4}, 
        {5, 53, 16, 12},
        {6, 81, 28, 4},
        {7, 113, 32, 4},
        {10, 261, 44, 12},
        {11, 317, 56, 4},
        {20, 1145, 100, 12},
        {21, 1257, 112, 4},
        {33, 3225, 180, 4},
        {37, 4085, 196, 12}
    };
    
    @Test public void testExpected() {
        long[] out = new long[3];
        for(int[] test: expected) {
            GaussCircle.classifyPoints(test[0], out);
            assertEquals(test[1], out[0]);
            assertEquals(test[2], out[1]);
            assertEquals(test[3], out[2]);
        }
    }
    
    @Test public void testClassifyPointsTen() {
        testClassifyPoints(10, 3659121734L);
    }
    
    @Test public void testClassifyPointsThousand() {
        testClassifyPoints(1000, 3685844941L);
    }
    
    private void testClassifyPoints(int n, long expected) {
        CRC32 check = new CRC32();
        long[] out = new long[3];
        Random rng = new Random(12345);
        int r = 1, step = 4, goal = 10;
        for(int i = 0; i < n; i++) {
            GaussCircle.classifyPoints(r, out);
            try {
                check.update(Long.toHexString(out[0]).getBytes("UTF-8"));
                check.update(Long.toHexString(out[1]).getBytes("UTF-8"));
                check.update(Long.toHexString(out[2]).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            r += rng.nextInt(step) + 1;
            if(r > goal) { goal = 8 * goal; step = step * 2; }
            assert r > 0; // make sure our r-values don't overflow
        }
        assertEquals(expected, check.getValue());
    }    
}