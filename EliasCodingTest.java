import org.junit.Test;
import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;
import java.util.Random;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EliasCodingTest {

    @Test public void testEliasGammaExplicit() {
        List<BigInteger> items = new ArrayList<>();
        items.add(BigInteger.ONE);
        items.add(new BigInteger("9"));
        items.add(new BigInteger("4"));
        items.add(new BigInteger("16"));
        items.add(new BigInteger("1234567"));
        String bits = EliasCoding.encodeEliasGamma(items);
        assertEquals("100010010010000001000000000000000000000000100101101011010000111", bits);
        List<BigInteger> back = EliasCoding.decodeEliasGamma(bits);
        assertEquals(items, back);
    }

    @Test public void testEliasOmegaExplicit() {
        List<BigInteger> items = new ArrayList<>();
        items.add(BigInteger.ONE);
        items.add(new BigInteger("9"));
        items.add(new BigInteger("4"));
        items.add(new BigInteger("16"));
        items.add(new BigInteger("1234567"));
        String bits = EliasCoding.encodeEliasOmega(items);
        assertEquals("011100101010001010010000010100101001001011010110100001110", bits);
        List<BigInteger> back = EliasCoding.decodeEliasOmega(bits);
        assertEquals(items, back);

        // From the Wikipedia page "Elias Omega Coding"
        BigInteger GOOGOL = BigInteger.TEN.pow(100);
        String googolBits = EliasCoding.encodeEliasOmega(Arrays.asList(GOOGOL));
        assertEquals("111000101001100" +
                "100100100100110101101001001011001010011000011011111001110101100001011001001111000010011000100" +
                "110011100000101111110011100010101100111001000000100011100010000100011010011111001010101010110010" +
                "010000110000100010101000001011101000111100010000000000000000000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000000000000"
                + "0", googolBits);
    }

    @Test public void testEliasGammaHundred() {
        test(100, 1339919739L, "gamma");
    }

    @Test public void testEliasGammaThousand() {
        test(1000, 3799119005L, "gamma");
    }

    @Test public void testEliasOmegaHundred() {
        test(100, 1347754805L, "omega");
    }

    @Test public void testEliasOmegaThousand() {
        test(1000, 1992917289L, "omega");
    }

    private void test(int n, long expected, String mode) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        BigInteger[] ints = new BigInteger[n];
        ints[0] = new BigInteger("2");
        ints[1] = new BigInteger("7");
        ints[2] = new BigInteger("11");
        ints[3] = BigInteger.TWO.pow(30); // Need a power of two to reveal certain edge case bugs.
        for(int i = 4; i < n; i++) {
            int j1 = rng.nextInt(i);
            int j2 = rng.nextInt(i);
            ints[i] = rng.nextBoolean() ? ints[j1].add(ints[j2]): ints[j1].multiply(ints[j2]);
        }
        int len = 2, count = 0, goal = 4;
        for(int i = 0; i < n; i++) {
            // Choose a random sequence of the previously constructed BigInteger values.
            List<BigInteger> items = new ArrayList<>();
            for(int j = 0; j < len; j++) {
                items.add(ints[rng.nextInt(n)]);
            }
            // Convert the sequence of BigInteger values to bits.
            String bits = "";
            if(mode.equals("gamma")) {
                bits = EliasCoding.encodeEliasGamma(items);
            }
            else if(mode.equals("omega")) {
                bits = EliasCoding.encodeEliasOmega(items);
            }
            else {
                System.out.println("The test is not working. This is not supposed to happen.");
                fail();
            }
            // Update the checksum with the returned bit pattern.
            try {
                check.update(bits.getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {
                fail();
            }

            // Convert bit pattern back to list of BigInteger values.
            List<BigInteger> back = null;
            if(mode.equals("gamma")) {
                back = EliasCoding.decodeEliasGamma(bits);
            }
            else if(mode.equals("omega")) {
                back = EliasCoding.decodeEliasOmega(bits);
            }

            // Decoded list from the bit pattern must equal the original list.
            assertEquals(items, back);

            // Counter for increasing the list length.
            if(++count == goal) {
                count = 0; goal = goal + 2; len++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}