import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class GaussCircleTest {

    @Test public void testClassifyPointsTen() {
        testClassifyPoints(10, 3659121734L);
    }
    
    @Test public void testClassifyPointsThousand() {
        testClassifyPoints(1000, 3685844941L);
    }
    
    @Test public void testClassifyPointsTenThousand() {
        testClassifyPoints(10_000, 2358886060L);
    }
    
    private void testClassifyPoints(int n, long expected) {
        CRC32 check = new CRC32();
        long[] out = new long[3];
        Random rng = new Random(12345);
        int r = 1, step = 4, goal = 10;
        for(int i = 0; i < n; i++) {
            GaussCircle.classifyPoints(r, out);
            //System.out.println(r + " " + Arrays.toString(out));
            check.update(Long.toHexString(out[0]).getBytes());
            check.update(Long.toHexString(out[1]).getBytes());
            check.update(Long.toHexString(out[2]).getBytes());
            r += rng.nextInt(step) + 1;
            if(r > goal) { goal = 8 * goal; step = step * 2; }
            assert r > 0; // make sure our r-values don't overflow
        }
        assertEquals(expected, check.getValue());
    }    
}