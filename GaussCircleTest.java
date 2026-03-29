import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class GaussCircleTest {

    // r, inside, border, edge
    private static final long[][] expected = {
            {1, 1, 0, 4},
            {2, 5, 4, 4},
            {3, 13, 12, 4},
            {5, 53, 16, 12},
            {6, 81, 28, 4},
            {7, 113, 32, 4},
            {10, 261, 44, 12},
            {11, 317, 56, 4},
            {15, 625, 72, 12},
            {20, 1145, 100, 12},
            {21, 1257, 112, 4},
            {25, 1821, 120, 20},
            {33, 3225, 180, 4},
            {37, 4085, 196, 12},
            {50, 7565, 260, 20},
            {100, 30853, 544, 20},
            {200, 124501, 1108, 20},
            {500, 782521, 2800, 28},
            {1000, 3135893, 5628, 28},
            {100000, 31415359773L, 565640, 44}
    };

    @Test public void testExpected() {
        long[] out = new long[3];
        for (long[] test : expected) {
            int r = (int) test[0];
            GaussCircle.classifyPoints(r, out);
            assertEquals("inside for r=" + r, test[1], out[0]);
            assertEquals("border for r=" + r, test[2], out[1]);
            assertEquals("edge for r=" + r, test[3], out[2]);
        }
    }

    @Test public void testClassifyPointsSmall() {
        testClassifyPoints(10, 3659121734L);
    }

    @Test(timeout = 10000)
    public void testClassifyPointsLarge() {
        testClassifyPoints(1000, 3685844941L);
    }

    @Test(timeout = 10000)
    public void testClassifyPointsLargest() {
        testClassifyPoints(3000, 1944193159L);
    }

    private void testClassifyPoints(int n, long expected) {
        CRC32 check = new CRC32();
        long[] out = new long[3];
        Random rng = new Random(12345);
        int r = 1, step = 4, goal = 10;
        for (int i = 0; i < n; i++) {
            GaussCircle.classifyPoints(r, out);
            check.update(Long.toHexString(out[0]).getBytes(StandardCharsets.UTF_8));
            check.update(Long.toHexString(out[1]).getBytes(StandardCharsets.UTF_8));
            check.update(Long.toHexString(out[2]).getBytes(StandardCharsets.UTF_8));
            r += rng.nextInt(step) + 1;
            if (r > goal) { goal = 8 * goal; step = step * 2; }
        }
        assertEquals(expected, check.getValue());
    }
}