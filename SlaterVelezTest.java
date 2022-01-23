import org.junit.Test;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class SlaterVelezTest {

    private static final long[] EXPECTED_PREFIX = {
            1, 2, 4, 7, 3, 8, 14, 5, 12, 20, 6, 16, 27, 9, 21, 34, 10, 25, 41, 11, 28, 47, 13, 33, 54, 15,
            37, 60, 17, 42, 68, 18, 45, 73, 19, 48, 79, 22, 55, 23, 58, 94, 24, 61, 99, 26, 66, 107, 29, 71,
            115, 30, 75, 121, 31, 78, 126, 32, 81, 132, 35, 87, 140, 36, 91, 147, 38, 96, 155, 39
    };

    @Test public void testOneThousand() {
        test(1000, 3428234694L);
    }

    @Test public void testTenThousand() {
        test(10_000, 2582796439L);
    }

    @Test public void testFiveHundredThousand() {
        test(500_000, 3035586797L);
    }

    @Test public void testPrefix() {
        SlaterVelez sv = new SlaterVelez();
        long[] result = new long[EXPECTED_PREFIX.length];
        for(int i = 0; i < EXPECTED_PREFIX.length; i++) {
            result[i] = sv.next();
        }
        assertArrayEquals(EXPECTED_PREFIX, result);
    }

    private void test(int n, long expected) {
        CRC32 check = new CRC32();
        SlaterVelez sv = new SlaterVelez();
        long v = 0;
        for(int i = 0; i < n; i++) {
            assertTrue(sv.hasNext());
            v = sv.next();
            check.update((int)(v & 0xFFFFFFFF));
        }
        assertEquals(expected, check.getValue());
    }
}