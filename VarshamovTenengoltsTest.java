import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class VarshamovTenengoltsTest {

    @Test public void testEncodeExplicit() {
        assertEquals("000", VarshamovTenengolts.encode("0"));
        assertEquals("101", VarshamovTenengolts.encode("1"));
        assertEquals("11100", VarshamovTenengolts.encode("10"));
        assertEquals("10001", VarshamovTenengolts.encode("01"));
        assertEquals("001011", VarshamovTenengolts.encode("111"));
        assertEquals("000000", VarshamovTenengolts.encode("000"));
        assertEquals("1010001000", VarshamovTenengolts.encode("100100"));
        assertEquals("110011001001100", VarshamovTenengolts.encode("01101001100"));
        assertEquals("0000001001000110111", VarshamovTenengolts.encode("00010100011111"));
        assertEquals("001001000011111001000100110001001111", VarshamovTenengolts.encode("101000111110100010011000101111"));
    }

    @Test public void testDecodeExplicit() {
        assertEquals("01", VarshamovTenengolts.decode("1001"));
        assertEquals("01", VarshamovTenengolts.decode("0001"));
        assertEquals("111", VarshamovTenengolts.decode("00011"));
        assertEquals("111", VarshamovTenengolts.decode("01011"));
        assertEquals("100100", VarshamovTenengolts.decode("101001000"));
        assertEquals("100100", VarshamovTenengolts.decode("101000000"));
        assertEquals("01101001100", VarshamovTenengolts.decode("11001100101100"));
        assertEquals("11001100000111", VarshamovTenengolts.decode("110100011000001111"));
        assertEquals("110000011110100", VarshamovTenengolts.decode("0011100000011100100"));
        assertEquals("101000111110100010011000101111", VarshamovTenengolts.decode("00100100001111001000100110001001111"));
    }

    @Test public void massTestOneThousand() {
        massTest(546136691L,1000);
    }

    @Test public void massTestOneMillion() {
        massTest(3477739903L,1000000);
    }

    private void massTest(long expected, int n) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        int m = 3, goal = 10;
        for(int i = 0; i < n; i++) {
            // Construct the random message of m bits for Alice.
            StringBuilder original = new StringBuilder();
            for(int j = 0; j < m; j++) {
                original.append(rng.nextBoolean() ? '0' : '1');
            }
            // Encode the original message for communication over single deletion channel.
            String encoded = VarshamovTenengolts.encode(original.toString());
            for(int j = 0; j < encoded.length(); j++) {
                check.update(encoded.charAt(j));
            }
            // Adversary now removes one character from a random position.
            int pos = rng.nextInt(m);
            String received = encoded.substring(0, pos) + encoded.substring(pos + 1);
            // Decode the received message, and verify that the result equals the original.
            String decoded = VarshamovTenengolts.decode(received);
            assertEquals(original.toString(), decoded);
            // Update the length of the generated random bit string.
            if(i == goal) {
                goal = (3 * goal) / 2; m++;
            }
        }
        assertEquals(expected, check.getValue());
    }

    // Systematic round trip test method supplied by Claude Opus 4.6.
    @Test
    public void testExhaustiveRoundTrip() {
        for (int bits = 1; bits <= 12; bits++) {
            for (int val = 0; val < (1 << bits); val++) {
                String original = toBitString(val, bits);
                String encoded = VarshamovTenengolts.encode(original);
                for (int delPos = 0; delPos < encoded.length(); delPos++) {
                    String received = encoded.substring(0, delPos) + encoded.substring(delPos + 1);
                    String decoded = VarshamovTenengolts.decode(received);
                    assertEquals(
                            "Failed for original=\"" + original + "\" delPos=" + delPos,
                            original, decoded
                    );
                }
            }
        }
    }

    private static String toBitString(int val, int bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = bits - 1; i >= 0; i--) {
            sb.append((val >> i) & 1);
        }
        return sb.toString();
    }
}
