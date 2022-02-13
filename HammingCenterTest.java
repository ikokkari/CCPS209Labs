import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class HammingCenterTest {

    private static String toBits(boolean[] bits) {
        String result = ""; // Too small test cases to bother to use StringBuilder really
        for(boolean b: bits) { result += b ? "1": "0"; }
        return result;
    }

    private static boolean[] fromBits(String bits) {
        int n = bits.length();
        boolean[] result = new boolean[n];
        for(int i = 0; i < n; i++) {
            result[i] = bits.charAt(i) == '1';
        }
        return result;
    }

    private static boolean[][] fromBits(String[] bits) {
        int n = bits.length;
        boolean[][] result = new boolean[n][bits[0].length()];
        for(int i = 0; i < n; i++) {
            result[i] = fromBits(bits[i]);
        }
        return result;
    }

    @Test public void testFindHammingCenterExplicit() {
        String[] bitS0 = {"1010", "0000", "1011", "0101"};
        boolean[][] bits0 = fromBits(bitS0);
        assertEquals(null, HammingCenter.findHammingCenter(bits0, 1));
        assertEquals("0011", toBits(HammingCenter.findHammingCenter(bits0, 2)));

        String[] bitS1 = {"001011", "101100", "000001", "101010", "011010", "010111"};
        boolean[][] bits1 = fromBits(bitS1);
        assertEquals(null, HammingCenter.findHammingCenter(bits1, 1));
        assertEquals(null, HammingCenter.findHammingCenter(bits1, 2));
        assertEquals("000110", toBits(HammingCenter.findHammingCenter(bits1, 3)));
    }

    @Test public void testFindHammingCenterOneHundred() {
        test(100, 2909231474L);
    }

    @Test public void testFindHammingCenterTwoHundred() {
        test(200, 4124715625L);
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