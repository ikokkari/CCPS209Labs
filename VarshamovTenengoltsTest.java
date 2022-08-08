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
        assertEquals("110011001001100", VarshamovTenengolts.encode("01101001100"));
    }

    @Test public void testDecodeExplicit() {
        assertEquals("01", VarshamovTenengolts.decode("1001"));
        assertEquals("01", VarshamovTenengolts.decode("0001"));
        assertEquals("111", VarshamovTenengolts.decode("00011"));
        assertEquals("111", VarshamovTenengolts.decode("01011"));
        assertEquals("01101001100", VarshamovTenengolts.decode("11001101001100"));
        assertEquals("01101001100", VarshamovTenengolts.decode("11001100100110"));
    }

    @Test public void massTestOneThousand() {
        massTest(1000);
    }

    @Test public void massTestOneMillion() {
        massTest(1000000);
    }

    private void massTest(int n) {
        Random rng = new Random(12345 + n);
        int m = 3, goal = 10;
        for(int i = 0; i < n; i++) {
            StringBuilder original = new StringBuilder();
            for(int j = 0; j < m; j++) {
                original.append(rng.nextBoolean() ? '0' : '1');
            }
            String encoded = VarshamovTenengolts.encode(original.toString());
            // Adversary now removes the character in a random position.
            int pos = rng.nextInt(m);
            String adversary = encoded.substring(0, pos) + encoded.substring(pos + 1);
            String decoded = VarshamovTenengolts.decode(adversary);
            assertEquals(original.toString(), decoded);
            if(i == goal) {
                goal = (3 * goal) / 2; m++;
            }
        }
    }
}