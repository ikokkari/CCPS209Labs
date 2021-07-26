import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class LinusSequenceTest {

    @Test public void testMaximalRepeatedSuffix() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < 500; i++) {
            int nn = i + 5;
            boolean[] bits = new boolean[nn];
            for(int j = 0; j <= i; j++) {
                int p = rng.nextInt(nn / 2);
                int q = nn - 2 * p;
                for(int k = 0; k < q; k++) {
                    bits[k] = rng.nextBoolean();
                }
                for(int k = 0; k < p; k++) {
                    boolean b = rng.nextBoolean();
                    bits[q + k] = bits[q + p + k] = b;
                }
                int result = LinusSequence.maximalRepeatedSuffix(bits, nn);
                check.update(result);
            }
        }
        assertEquals(2544580030L, check.getValue());
    }

    private static String toStr(boolean[] seq) {
        StringBuilder result = new StringBuilder();
        for (boolean b : seq) {
            result.append(b ? '2' : '1');
        }
        return result.toString();
    }
    
    @Test public void testLinusSequencePrefix() {
        String expected = // First thousand elements of Linus sequence for the alphabet "12"
          "121122121121221121112212112211121122121121221121112212112212221121221122212211212212112"
        + "212221121221122212211121122121121221121112212112211121122121121221121112212221121221122"
        + "212211212212112212221121221122212211212211121122121121221121112212112211121122121121221"
        + "121112212221121221122212211212212112212221121221122212212112212221121221121112212112211"
        + "121122121121221121112212112211121122212211212212112212221121221122212211212212112212221"
        + "121112212112211121122121121221121112212112211121122121122212211212212112212221121221122"
        + "212211212212112212221121112212112211121122121121221121112212112211121121221121112212112"
        + "212221121221122212211212212112212221121221122212211121122121121221121112212112211121122"
        + "121121221121112212221121221122212211212212112212221121221122212211212211121122121121221"
        + "121112212112211121122121121221121112212221121221122212211212212112212221121221122212212"
        + "112212221121221121112212112211121122121121221121112212112211121122212211212212112212221"
        + "1212211222122112122121122122211211122121122";
        String result = toStr(LinusSequence.linusSequence(1000));
        assertEquals(expected, result);
    }
    
    @Test public void testLinusSequenceTwentyThousand() {
        CRC32 check = new CRC32();
        boolean[] result = LinusSequence.linusSequence(20000);
        for(int i = 0; i < result.length; i++) {
            check.update(result[i] ? i : -i);
        }
        assertEquals(1764336421L, check.getValue());
    }    
}