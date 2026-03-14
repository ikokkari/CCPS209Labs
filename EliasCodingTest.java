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

    // --- Elias Gamma explicit tests ---

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

    @Test public void testEliasGammaOne() {
        // x=1: N=0, no prefix zeros, encoding is just "1"
        List<BigInteger> items = Arrays.asList(BigInteger.ONE);
        assertEquals("1", EliasCoding.encodeEliasGamma(items));
        assertEquals(items, EliasCoding.decodeEliasGamma("1"));
    }

    @Test public void testEliasGammaSmallValues() {
        // x=2: "010", x=3: "011", x=4: "00100"
        assertEquals("010", EliasCoding.encodeEliasGamma(Arrays.asList(new BigInteger("2"))));
        assertEquals("011", EliasCoding.encodeEliasGamma(Arrays.asList(new BigInteger("3"))));
        assertEquals("00100", EliasCoding.encodeEliasGamma(Arrays.asList(new BigInteger("4"))));
    }

    @Test public void testEliasGammaPowersOfTwo() {
        // Powers of 2: encoding is N zeros followed by 1 followed by N zeros
        assertEquals("1", EliasCoding.encodeEliasGamma(Arrays.asList(BigInteger.ONE)));
        assertEquals("010", EliasCoding.encodeEliasGamma(Arrays.asList(new BigInteger("2"))));
        assertEquals("00100", EliasCoding.encodeEliasGamma(Arrays.asList(new BigInteger("4"))));
        assertEquals("0001000", EliasCoding.encodeEliasGamma(Arrays.asList(new BigInteger("8"))));
        assertEquals("000010000", EliasCoding.encodeEliasGamma(Arrays.asList(new BigInteger("16"))));
    }

    @Test public void testEliasGammaSequenceOfOnes() {
        // Multiple 1s: each encoded as "1", so "111" = [1,1,1]
        List<BigInteger> ones = Arrays.asList(BigInteger.ONE, BigInteger.ONE, BigInteger.ONE);
        assertEquals("111", EliasCoding.encodeEliasGamma(ones));
        assertEquals(ones, EliasCoding.decodeEliasGamma("111"));
    }

    @Test public void testEliasGammaMixedSequence() {
        List<BigInteger> seq = Arrays.asList(
                BigInteger.ONE, new BigInteger("2"), new BigInteger("3"),
                new BigInteger("7"), new BigInteger("15"), new BigInteger("31"));
        String bits = EliasCoding.encodeEliasGamma(seq);
        assertEquals("1010011001110001111000011111", bits);
        assertEquals(seq, EliasCoding.decodeEliasGamma(bits));
    }

    @Test public void testEliasGammaMaxByte() {
        // 255 = 11111111 in binary, N=7
        List<BigInteger> items = Arrays.asList(new BigInteger("255"));
        String bits = EliasCoding.encodeEliasGamma(items);
        assertEquals("000000011111111", bits);
        assertEquals(items, EliasCoding.decodeEliasGamma(bits));
    }

    @Test public void testEliasGammaEmptyList() {
        // Empty list encodes to empty string
        List<BigInteger> empty = new ArrayList<>();
        assertEquals("", EliasCoding.encodeEliasGamma(empty));
        assertEquals(empty, EliasCoding.decodeEliasGamma(""));
    }

    @Test public void testEliasGammaSingletonRoundTrip() {
        // Round-trip for various single values
        for (int x : new int[]{1, 2, 3, 5, 10, 42, 100, 1000, 65535}) {
            List<BigInteger> items = Arrays.asList(new BigInteger("" + x));
            String bits = EliasCoding.encodeEliasGamma(items);
            assertEquals(items, EliasCoding.decodeEliasGamma(bits));
        }
    }

    // --- Elias Omega explicit tests ---

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
    }

    @Test public void testEliasOmegaOne() {
        // x=1: encoding is just "0" (terminal marker)
        List<BigInteger> items = Arrays.asList(BigInteger.ONE);
        assertEquals("0", EliasCoding.encodeEliasOmega(items));
        assertEquals(items, EliasCoding.decodeEliasOmega("0"));
    }

    @Test public void testEliasOmegaSmallValues() {
        // x=2: "100", x=3: "110", x=4: "101000"
        assertEquals("100", EliasCoding.encodeEliasOmega(Arrays.asList(new BigInteger("2"))));
        assertEquals("110", EliasCoding.encodeEliasOmega(Arrays.asList(new BigInteger("3"))));
        assertEquals("101000", EliasCoding.encodeEliasOmega(Arrays.asList(new BigInteger("4"))));
    }

    @Test public void testEliasOmegaSmallRange() {
        // Verify all values 1-16 round-trip correctly
        for (int x = 1; x <= 16; x++) {
            List<BigInteger> items = Arrays.asList(new BigInteger("" + x));
            String bits = EliasCoding.encodeEliasOmega(items);
            List<BigInteger> back = EliasCoding.decodeEliasOmega(bits);
            assertEquals("Omega round-trip failed for " + x, items, back);
        }
    }

    @Test public void testEliasOmegaSequenceOfOnes() {
        // Multiple 1s: each encoded as "0", so "000" = [1,1,1]
        List<BigInteger> ones = Arrays.asList(BigInteger.ONE, BigInteger.ONE, BigInteger.ONE);
        assertEquals("000", EliasCoding.encodeEliasOmega(ones));
        assertEquals(ones, EliasCoding.decodeEliasOmega("000"));
    }

    @Test public void testEliasOmegaGoogol() {
        // From the Wikipedia page "Elias Omega Coding"
        BigInteger GOOGOL = BigInteger.TEN.pow(100);
        String googolBits = EliasCoding.encodeEliasOmega(Arrays.asList(GOOGOL));
        assertEquals("111000101001100" +
                "100100100100110101101001001011001010011000011011111001110101100001011001001111000010011000100" +
                "110011100000101111110011100010101100111001000000100011100010000100011010011111001010101010110010" +
                "010000110000100010101000001011101000111100010000000000000000000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000000000000"
                + "0", googolBits);
        assertEquals(Arrays.asList(GOOGOL), EliasCoding.decodeEliasOmega(googolBits));
    }

    @Test public void testEliasOmegaPowerOfTwo() {
        // 1024 = 2^10
        List<BigInteger> items = Arrays.asList(new BigInteger("1024"));
        String bits = EliasCoding.encodeEliasOmega(items);
        assertEquals("111010100000000000", bits);
        assertEquals(items, EliasCoding.decodeEliasOmega(bits));
    }

    @Test public void testEliasOmegaMixedSequence() {
        List<BigInteger> seq = Arrays.asList(
                BigInteger.ONE, new BigInteger("2"), new BigInteger("3"),
                new BigInteger("4"), new BigInteger("5"),
                new BigInteger("10"), new BigInteger("100"), new BigInteger("1000"));
        String bits = EliasCoding.encodeEliasOmega(seq);
        List<BigInteger> back = EliasCoding.decodeEliasOmega(bits);
        assertEquals(seq, back);
    }

    @Test public void testEliasOmegaEmptyList() {
        List<BigInteger> empty = new ArrayList<>();
        assertEquals("", EliasCoding.encodeEliasOmega(empty));
        assertEquals(empty, EliasCoding.decodeEliasOmega(""));
    }

    // --- Cross-coding comparison ---

    @Test public void testOmegaMoreCompactThanGamma() {
        // For large numbers, omega encoding should be shorter than gamma
        BigInteger big = BigInteger.TWO.pow(100);
        String gamma = EliasCoding.encodeEliasGamma(Arrays.asList(big));
        String omega = EliasCoding.encodeEliasOmega(Arrays.asList(big));
        // Gamma doubles the bits (100 zeros + 101 bits = 201), omega is much shorter
        assertEquals(201, gamma.length());
        assertTrue("Omega should be shorter than gamma for 2^100",
                omega.length() < gamma.length());
    }

    private static void assertTrue(String msg, boolean cond) {
        if (!cond) fail(msg);
    }

    // --- CRC mass tests ---

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
        ints[3] = BigInteger.TWO.pow(30);
        for(int i = 4; i < n; i++) {
            int j1 = rng.nextInt(i);
            int j2 = rng.nextInt(i);
            ints[i] = rng.nextBoolean() ? ints[j1].add(ints[j2]): ints[j1].multiply(ints[j2]);
        }
        int len = 2, count = 0, goal = 4;
        for(int i = 0; i < n; i++) {
            List<BigInteger> items = new ArrayList<>();
            for(int j = 0; j < len; j++) {
                items.add(ints[rng.nextInt(n)]);
            }
            String bits = "";
            if(mode.equals("gamma")) {
                bits = EliasCoding.encodeEliasGamma(items);
            } else if(mode.equals("omega")) {
                bits = EliasCoding.encodeEliasOmega(items);
            } else { fail(); }
            try {
                check.update(bits.getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) { fail(); }
            List<BigInteger> back = null;
            if(mode.equals("gamma")) {
                back = EliasCoding.decodeEliasGamma(bits);
            } else if(mode.equals("omega")) {
                back = EliasCoding.decodeEliasOmega(bits);
            }
            assertEquals(items, back);
            if(++count == goal) {
                count = 0; goal = goal + 2; len++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}