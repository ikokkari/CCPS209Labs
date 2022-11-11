import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.zip.CRC32;

public class InventorySequenceTest {

    @Test public void testExplicit() {
        // First 14 elements, from the Numberphile video.
        int[] expected0 = {
                0, 1, 1, 0, 2, 2, 2, 0, 3, 2, 4, 1, 1, 0
        };
        int[] actual0 = InventorySequence.inventorySequence(0, expected0.length);
        assertArrayEquals(expected0, actual0);

        // First 85 elements, copied from https://oeis.org/A342585
        int[] expected1 = {
                0, 1, 1, 0, 2, 2, 2, 0, 3, 2, 4, 1, 1, 0, 4, 4, 4, 1, 4, 0, 5, 5, 4, 1, 6, 2, 1, 0, 6,
                7, 5, 1, 6, 3, 3, 1, 0, 7, 9, 5, 3, 6, 4, 4, 2, 0, 8, 9, 6, 4, 9, 4, 5, 2, 1, 3, 0, 9,
                10, 7, 5, 10, 6, 6, 3, 1, 4, 2, 0, 10, 11, 8, 6, 11, 6, 9, 3, 2, 5, 3, 2, 0, 11, 11, 10
        };
        int[] actual1 = InventorySequence.inventorySequence(0, expected1.length);
        assertArrayEquals(expected1, actual1);

        // A window of 30 elements starting from position 1000.
        int[] expected2 = {
                10, 19, 14, 12, 14, 13, 10, 5, 4, 4, 6, 7, 4, 2, 1, 1, 1, 0, 43, 38, 44, 31, 50, 36, 42,
                32, 34, 38, 35, 35
        };
        int[] actual2 = InventorySequence.inventorySequence(1000, 1000 + expected2.length);
        assertArrayEquals(expected2, actual2);

        // A window of 30 elements starting from position 10000.
        int[] expected3 = {
                17, 17, 15, 24, 20, 16, 16, 16, 11, 15, 14, 10, 14, 17, 16, 9, 9, 14, 13, 13, 17, 8, 6,
                16, 8, 8, 9, 3, 11, 7, 6, 11, 5, 7, 6, 9, 5, 8, 6, 8, 6, 7, 5, 5, 4, 2, 6, 5, 5, 4
        };
        int[] actual3 = InventorySequence.inventorySequence(10000, 10000 + expected3.length);
        assertArrayEquals(expected3, actual3);

        // A window of 100 elements starting from position 100000.
        int[] expected4 = {
                56, 54, 51, 58, 48, 58, 39, 59, 41, 31, 39, 34, 34, 34, 39, 42, 27, 38, 38, 31, 29, 31,
                36, 34, 34, 31, 38, 18, 26, 29, 39, 20, 24, 30, 33, 18, 34, 22, 27, 30, 28, 44, 26, 27,
                30, 17, 24, 19, 22, 12, 19, 19, 16, 17, 23, 18, 18, 20, 19, 22, 23, 14, 19, 21, 25, 20,
                22, 25, 15, 22, 19, 14, 16, 19, 20, 35, 16, 14, 15, 27, 18, 21, 17, 19, 16, 15, 20, 19,
                11, 17, 20, 14, 19, 19, 19, 17, 14, 13, 12, 20
        };
        int[] actual4 = InventorySequence.inventorySequence(100000, 100000 + expected4.length);
        assertArrayEquals(expected4, actual4);
    }

    @Test public void massTestMillion() {
        massTest(1_000_000, 1_100_000, 3241069703L);
    }

    @Test public void massTestTenMillion() {
        massTest(10_000_000, 10_100_000, 4068686297L);
    }

    @Test public void massTestHundredMillion() {
        massTest(100_000_000, 100_100_000, 926087478L);
    }

    @Test public void massTestTwoBillion() {
        massTest(2_000_000_000, 2_001_234_567, 3176355533L);
    }

    private void massTest(int start, int end, long expected) {
        CRC32 check = new CRC32();
        for(int e: InventorySequence.inventorySequence(start, end)) {
            check.update(e);
        }
        assertEquals(expected, check.getValue());
    }
}