import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HammingCenterTest {

    private static String toBits(boolean[] bits) {
        StringBuilder sb = new StringBuilder(bits.length);
        for(boolean b: bits) { sb.append(b ? '1' : '0'); }
        return sb.toString();
    }

    private static boolean[] fromBits(String bits) {
        int n = bits.length();
        boolean[] result = new boolean[n];
        for(int i = 0; i < n; i++) {
            result[i] = bits.charAt(i) == '1';
        }
        return result;
    }

    private static boolean[][] fromBits(String... bits) {
        boolean[][] result = new boolean[bits.length][bits[0].length()];
        for(int i = 0; i < bits.length; i++) {
            result[i] = fromBits(bits[i]);
        }
        return result;
    }

    private static int hammingDistance(boolean[] a, boolean[] b) {
        int d = 0;
        for(int i = 0; i < a.length; i++) {
            if(a[i] != b[i]) { d++; }
        }
        return d;
    }

    // Verify that center is within radius r of every row, and is lex-lowest.
    private static void verifyCenter(boolean[][] bits, int r, boolean[] center, String expectedStr) {
        assertNotNull("Expected a center but got null", center);
        assertEquals(expectedStr, toBits(center));
        for(int i = 0; i < bits.length; i++) {
            assertTrue("Center exceeds radius r=" + r + " for row " + i,
                    hammingDistance(center, bits[i]) <= r);
        }
    }

    // --- Explicit tests ---

    @Test public void testSpecExampleFourBits() {
        boolean[][] bits = fromBits("1010", "0000", "1011", "0101");
        assertNull(HammingCenter.findHammingCenter(bits, 1));
        verifyCenter(bits, 2, HammingCenter.findHammingCenter(bits, 2), "0011");
    }

    @Test public void testSpecExampleSixBits() {
        boolean[][] bits = fromBits("001011", "101100", "000001", "101010", "011010", "010111");
        assertNull(HammingCenter.findHammingCenter(bits, 1));
        assertNull(HammingCenter.findHammingCenter(bits, 2));
        verifyCenter(bits, 3, HammingCenter.findHammingCenter(bits, 3), "000110");
    }

    @Test public void testSingleRow() {
        // One row: the center is exactly that row at r = 0.
        boolean[][] bits = {fromBits("10110")};
        verifyCenter(bits, 0, HammingCenter.findHammingCenter(bits, 0), "10110");
    }

    @Test public void testSingleBitArrays() {
        // Two single-bit rows: {0} and {1}. r=0 impossible, r=1 gives "0" (lex lowest).
        boolean[][] bits = fromBits("0", "1");
        assertNull(HammingCenter.findHammingCenter(bits, 0));
        verifyCenter(bits, 1, HammingCenter.findHammingCenter(bits, 1), "0");
    }

    @Test public void testTwoComplementaryRows() {
        // "00" and "11": r=0 fails, r=1 gives "00" or "01" — lex lowest is "00".
        boolean[][] bits = fromBits("00", "11");
        assertNull(HammingCenter.findHammingCenter(bits, 0));
        verifyCenter(bits, 1, HammingCenter.findHammingCenter(bits, 1), "01");
    }

    @Test public void testRadiusZeroNotAllIdentical() {
        // Rows differ: no center at r = 0.
        boolean[][] bits = fromBits("101", "100");
        assertNull(HammingCenter.findHammingCenter(bits, 0));
        verifyCenter(bits, 1, HammingCenter.findHammingCenter(bits, 1), "100");
    }

    @Test public void testLargerRadiusThanNeeded() {
        // r is generous; should still return the lex-lowest center.
        boolean[][] bits = fromBits("111", "000");
        // Minimum radius is 2 (centers "001","010","011","100","101","110" all work at r≤2;
        // lex lowest at r=2 is "001"). At r=3, lex lowest is "000".
        verifyCenter(bits, 2, HammingCenter.findHammingCenter(bits, 2), "001");
        verifyCenter(bits, 3, HammingCenter.findHammingCenter(bits, 3), "000");
    }

    @Test public void testThreeRowsNeedingBacktrack() {
        // Setting false greedily at every position won't always work.
        // "111", "110", "001": center at r=1 impossible, r=2 lex-lowest is "010".
        boolean[][] bits = fromBits("111", "110", "001");
        assertNull(HammingCenter.findHammingCenter(bits, 1));
        verifyCenter(bits, 2, HammingCenter.findHammingCenter(bits, 2), "010");
    }

    @Test public void testFourRowsFiveBits() {
        boolean[][] bits = fromBits("00000", "11111", "10101", "01010");
        assertNull(HammingCenter.findHammingCenter(bits, 1));
        assertNull(HammingCenter.findHammingCenter(bits, 2));
        verifyCenter(bits, 3, HammingCenter.findHammingCenter(bits, 3), "00011");
    }

    @Test public void testAllZerosAndAllOnes() {
        // "0000" and "1111": radius 2, lex lowest center is "0011".
        boolean[][] bits = fromBits("0000", "1111");
        assertNull(HammingCenter.findHammingCenter(bits, 1));
        verifyCenter(bits, 2, HammingCenter.findHammingCenter(bits, 2), "0011");
    }

    // --- Mass / CRC tests ---

    @Test public void testFindHammingCenterOneHundred() {
        test(100, 2909231474L);
    }

    @Test public void testFindHammingCenterTwoHundred() {
        test(200, 4124715625L);
    }

    @Test public void testFindHammingCenterThreeHundred() {
        test(300, 1484491021L);
    }

    private void test(int trials, long expected) {
        Random rng = new Random(12345 + trials);
        CRC32 check = new CRC32();
        int n = 3, count = 0, goal = 3;
        for(int i = 0; i < trials; i++) {
            // Create rows of random bits.
            int m = n + rng.nextInt(4);
            boolean[][] bits = new boolean[n][m];
            for(int j1 = 0; j1 < n; j1++) {
                for(int j2 = 0; j2 < m; j2++) {
                    bits[j1][j2] = rng.nextBoolean();
                }
            }
            // Increase radius until a Hamming center has been found.
            for(int r = 1; r < m; r++) {
                boolean[] center = HammingCenter.findHammingCenter(bits, r);
                if(center != null) {
                    // Update the checksum with radius and the bits of the found center.
                    check.update(r);
                    for(boolean b: center) { check.update(b ? 42: 99); }
                    break;
                }
            }
            if(++count == goal) { n++; count = 0; goal++; }
        }
        assertEquals(expected, check.getValue());
    }
}